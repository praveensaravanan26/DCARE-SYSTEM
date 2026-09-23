"""
DCARE Document Processing Engine
Performs:
1. File Text Extraction (pdfplumber for native PDF, pytesseract for scanned images/PDFs)
2. Document Classification (POLICY_DOCUMENT, CLAIM_FORM, ACCIDENT_REPORT, REPAIR_INVOICE, DRIVING_LICENSE, VEHICLE_RC)
3. Structured Field Extraction (Regex, boundary tokens, semantic key-value patterns)
"""

import os
import re
from typing import Dict, Any, Tuple
import pdfplumber
from PIL import Image
import pytesseract

def extract_text_from_file(file_path: str) -> Tuple[str, float]:
    """
    Extracts raw text from PDF or image file.
    Returns (extracted_text, confidence_score)
    """
    if not os.path.exists(file_path):
        return "", 0.0

    ext = os.path.splitext(file_path)[1].lower()
    raw_text = ""
    confidence = 0.98

    try:
        if ext == '.pdf':
            with pdfplumber.open(file_path) as pdf:
                for page in pdf.pages:
                    text = page.extract_text()
                    if text:
                        raw_text += text + "\n"
            
            # Fallback to OCR if minimal text is extractable
            if len(raw_text.strip()) < 30:
                with pdfplumber.open(file_path) as pdf:
                    for page in pdf.pages:
                        img = page.to_image(resolution=200).original
                        ocr_text = pytesseract.image_to_string(img)
                        raw_text += ocr_text + "\n"
                confidence = 0.88
        elif ext in ['.png', '.jpg', '.jpeg']:
            img = Image.open(file_path)
            raw_text = pytesseract.image_to_string(img)
            confidence = 0.92
    except Exception as e:
        print(f"Extraction error for {file_path}: {e}")
        confidence = 0.70

    return raw_text.strip(), confidence

def classify_document(raw_text: str, fallback_type: str = "OTHER") -> str:
    """
    Classifies the document based on key terms and document structure.
    """
    text_lower = raw_text.lower()
    
    if any(k in text_lower for k in ["insurance policy schedule", "policy certificate", "coverage schedule", "period of insurance"]):
        return "POLICY_DOCUMENT"
    elif any(k in text_lower for k in ["claim notification", "motor claim form", "claim form", "notice of loss"]):
        return "CLAIM_FORM"
    elif any(k in text_lower for k in ["accident report", "accident incident report", "first information report", "police report"]):
        return "ACCIDENT_REPORT"
    elif any(k in text_lower for k in ["tax invoice", "repair invoice", "final repair bill", "parts and labor", "repair summary"]):
        return "REPAIR_INVOICE"
    elif any(k in text_lower for k in ["estimate", "repair estimate", "quotation"]):
        return "REPAIR_ESTIMATE"
    elif any(k in text_lower for k in ["driving licence", "driving license", "dl no"]):
        return "DRIVING_LICENSE"
    elif any(k in text_lower for k in ["certificate of registration", "vehicle registration", "rc book"]):
        return "VEHICLE_RC"
    
    return fallback_type

def extract_structured_fields(raw_text: str, doc_type: str) -> Dict[str, Any]:
    """
    Extracts high-value structured entities using regex, boundary tokens, and semantic patterns.
    """
    fields = {}
    lines = raw_text.splitlines()

    for line in lines:
        line_clean = line.strip()
        if not line_clean:
            continue
            
        # 1. Policy Number
        m = re.search(r'Policy\s*(?:No|Number|#)?\s*:\s*([A-Z0-9\-]{5,20})', line_clean, re.IGNORECASE)
        if m:
            val = m.group(1).strip()
            if not val.lower().startswith("holder"):
                fields['policy_number'] = val

        # 2. Claim Number
        m = re.search(r'Claim\s*(?:No|Number|#)?\s*:\s*(CLM-[A-Z0-9\-]{4,20})', line_clean, re.IGNORECASE)
        if m:
            fields['claim_number'] = m.group(1).strip()

        # 3. Vehicle Registration Number
        m = re.search(r'Vehicle\s*(?:Registration\s*No|Reg\s*No|No)?\s*:\s*([A-Z]{2}[-\s]?[0-9]{1,2}[-\s]?[A-Z]{1,3}[-\s]?[0-9]{4})', line_clean, re.IGNORECASE)
        if m:
            fields['vehicle_number'] = re.sub(r'\s+', '-', m.group(1).strip().upper())

        # 4. Dates
        m = re.search(r'(?:Incident\s*Date|Invoice\s*Date|Date\s*of\s*Loss|Date)\s*:\s*(202[0-9][-/.](?:0[1-9]|1[0-2])[-/.](?:0[1-9]|[12][0-9]|3[01]))', line_clean, re.IGNORECASE)
        if m:
            d_str = m.group(1).replace('/', '-').replace('.', '-')
            if 'REPAIR_INVOICE' in doc_type or 'Invoice Date' in line_clean:
                fields['invoice_date'] = d_str
            else:
                fields['incident_date'] = d_str

        # 5. Amounts
        m = re.search(r'(?:Total\s*Amount|Claimed\s*Amount|Total)\s*:\s*[\$₹€USD\s]*([0-9,]+(?:\.[0-9]{2})?)', line_clean, re.IGNORECASE)
        if m:
            amt_str = m.group(1).replace(',', '')
            try:
                amt_val = float(amt_str)
                if 'REPAIR_INVOICE' in doc_type or 'REPAIR_ESTIMATE' in doc_type or 'Total Amount' in line_clean:
                    fields['invoice_amount'] = amt_val
                else:
                    fields['claimed_amount'] = amt_val
            except ValueError:
                pass

        # 6. Invoice Number
        m = re.search(r'Invoice\s*(?:No|Number|#)?\s*:\s*(INV-[A-Z0-9\-_]{3,20})', line_clean, re.IGNORECASE)
        if m:
            fields['invoice_number'] = m.group(1).strip()

        # 7. Garage / Workshop Name
        m = re.search(r'(?:Workshop\s*/\s*Garage|Garage\s*Name|Workshop)\s*:\s*([A-Za-z0-9\s&,\.\-]{4,40})', line_clean, re.IGNORECASE)
        if m:
            fields['garage_name'] = m.group(1).strip()

        # 8. Customer / Policyholder Name
        m = re.search(r'(?:Policyholder\s*Name|Customer\s*Name|Driver\s*Insured|Policyholder)\s*:\s*([A-Za-z\s\.]{3,35})', line_clean, re.IGNORECASE)
        if m:
            fields['customer_name'] = m.group(1).strip()

    return fields
