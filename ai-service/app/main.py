"""
DCARE AI Service - FastAPI Application
Provides REST endpoints for OCR, document intelligence, consistency verification,
hybrid ML risk scoring, and SHAP explainability.
"""

import os
import sys

# Ensure local imports resolve
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from fastapi import FastAPI, HTTPException, Body
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import Dict, Any, List, Optional

from preprocessing.document_processor import extract_text_from_file, classify_document, extract_structured_fields
from preprocessing.consistency_engine import ConsistencyEngine
from inference.risk_engine import RiskEngine

app = FastAPI(
    title="DCARE AI & Document Intelligence Service",
    description="Automated Insurance Claim Processing, Document Intelligence, Cross-Document Consistency & Explainable Risk Evaluation",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

MODELS_DIR = "/Users/praveensaravanan/Desktop/DCARE_Final_Year_Project/models"
risk_engine = None

@app.on_event("startup")
def load_resources():
    global risk_engine
    try:
        risk_engine = RiskEngine(MODELS_DIR)
        print("[+] DCARE AI Models & SHAP Engine Loaded Successfully.")
    except Exception as e:
        print(f"[!] Warning: Could not initialize RiskEngine on startup: {e}")

class OcrRequest(BaseModel):
    file_path: str
    document_type: Optional[str] = "OTHER"

class FullAssessmentRequest(BaseModel):
    claim_id: str
    claim_number: str
    policy_number: str
    vehicle_number: str
    incident_date: str
    claimed_amount: float
    garage_name: Optional[str] = ""
    invoice_number: Optional[str] = ""
    documents: List[Dict[str, Any]] = []
    extracted_fields: Dict[str, Any] = {}
    historical_features: Dict[str, Any] = {}

@app.get("/health")
def health_check():
    return {
        "status": "UP",
        "service": "DCARE AI Service",
        "models_loaded": risk_engine is not None
    }

@app.post("/ocr")
def process_ocr(req: OcrRequest):
    if not os.path.exists(req.file_path):
        raise HTTPException(status_code=404, detail=f"File not found: {req.file_path}")
    
    raw_text, confidence = extract_text_from_file(req.file_path)
    doc_type = classify_document(raw_text, req.document_type)
    fields = extract_structured_fields(raw_text, doc_type)
    
    return {
        "raw_text": raw_text,
        "document_type": doc_type,
        "confidence": confidence,
        "fields": fields
    }

@app.post("/extract")
def extract_fields_endpoint(payload: Dict[str, Any] = Body(...)):
    raw_text = payload.get("raw_text", "")
    doc_type = payload.get("document_type", "OTHER")
    fields = extract_structured_fields(raw_text, doc_type)
    return {"fields": fields}

@app.post("/validate")
def validate_consistency_endpoint(payload: Dict[str, Any] = Body(...)):
    claim_data = payload.get("claim_data", {})
    documents = payload.get("documents", [])
    extracted_fields = payload.get("extracted_fields", {})
    
    results = ConsistencyEngine.evaluate(claim_data, documents, extracted_fields)
    return results

@app.post("/full-assessment")
def full_assessment_endpoint(req: FullAssessmentRequest):
    global risk_engine
    if risk_engine is None:
        risk_engine = RiskEngine(MODELS_DIR)
        
    claim_dict = {
        'claim_id': req.claim_id,
        'claim_number': req.claim_number,
        'policy_number': req.policy_number,
        'vehicle_number': req.vehicle_number,
        'incident_date': req.incident_date,
        'claimed_amount': req.claimed_amount,
        'garage_name': req.garage_name,
        'invoice_number': req.invoice_number
    }
    
    # 1. Run cross-document consistency engine
    cross_doc_res = ConsistencyEngine.evaluate(claim_dict, req.documents, req.extracted_fields)
    
    # 2. Synthesize complete feature set for ML models
    hist = req.historical_features
    zscore = float(hist.get('claim_amount_zscore', 0.0))
    if zscore == 0.0:
        hist_avg = float(hist.get('historical_avg_amount', 25000.0))
        zscore = (req.claimed_amount - hist_avg) / 15000.0
        
    features = {
        'claim_amount': float(req.claimed_amount),
        'historical_avg_amount': float(hist.get('historical_avg_amount', 25000.0)),
        'amount_deviation': float(req.claimed_amount) - float(hist.get('historical_avg_amount', 25000.0)),
        'claim_amount_zscore': float(zscore),
        'claim_frequency_90d': int(hist.get('claim_frequency_90d', 0)),
        'days_since_last_claim': int(hist.get('days_since_previous_claim', 365)),
        'previous_claim_count': int(hist.get('previous_claim_count', 0)),
        'garage_repeat_count': int(hist.get('garage_repeat_count', 0)),
        'document_mismatch_count': int(cross_doc_res['mismatch_count']),
        'duplicate_indicator': int(cross_doc_res['duplicate_indicator']),
        'invoice_reuse_indicator': int(cross_doc_res['invoice_reuse_indicator']),
        'policy_age_days': int(hist.get('policy_age_days', 420)),
        'incident_to_claim_delay_days': int(hist.get('incident_to_claim_delay_days', 2))
    }
    
    # 3. Run multi-model assessment and explainability
    assessment = risk_engine.assess_claim(features, cross_doc_res)
    
    return assessment

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
