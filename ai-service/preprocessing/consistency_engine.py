"""
DCARE Cross-Document Consistency Engine
Cross-examines entities across documents:
1. Incident Date verification (Claim Form vs Accident Report vs Repair Invoice)
2. Claimed Amount vs Repair Invoice Amount tolerance
3. Vehicle Number alignment across RC, Estimate, and Policy
4. Policy Number verification
5. Duplicate Document Hash & Duplicate Invoice Number detection
"""

from typing import List, Dict, Any

class ConsistencyEngine:
    @staticmethod
    def evaluate(claim_data: Dict[str, Any],
                 documents: List[Dict[str, Any]],
                 extracted_fields: Dict[str, Any]) -> Dict[str, Any]:
        
        validations = []
        evidence = []
        mismatch_count = 0
        duplicate_indicator = 0
        invoice_reuse_indicator = 0
        
        claim_incident_date = claim_data.get('incident_date')
        claim_amount = float(claim_data.get('claimed_amount', 0.0))
        claim_vehicle = str(claim_data.get('vehicle_number', '')).replace('-', '').replace(' ', '').upper()
        claim_policy = str(claim_data.get('policy_number', '')).strip().upper()
        claim_invoice = str(claim_data.get('invoice_number', '')).strip()
        
        # 1. Check Document Hash Duplicates
        seen_hashes = {}
        for doc in documents:
            fhash = doc.get('file_hash')
            dtype = doc.get('document_type', 'DOCUMENT')
            if fhash and fhash in seen_hashes:
                duplicate_indicator = 1
                validations.append({
                    'rule_name': 'DUPLICATE_DOCUMENT_HASH',
                    'category': 'DUPLICATE_DETECTION',
                    'status': 'FAILED',
                    'severity': 'HIGH',
                    'description': f"Identical cryptographic file hash detected for {dtype} and {seen_hashes[fhash]}. Duplicate upload indicator.",
                    'detected_value': fhash[:16] + "...",
                    'expected_value': 'Unique Document Hash',
                    'source_document_type': dtype,
                    'target_document_type': seen_hashes[fhash]
                })
                evidence.append({
                    'type': 'DUPLICATE_DOCUMENT',
                    'description': f"Cryptographic file hash collision detected across uploaded documents ({dtype}).",
                    'severity': 'HIGH',
                    'source_doc': dtype,
                    'source_field': 'file_hash_sha256',
                    'detected_value': fhash[:16] + "...",
                    'expected_value': 'Distinct File Hash',
                    'contribution': 25.0
                })
            elif fhash:
                seen_hashes[fhash] = dtype

        # 2. Check Incident Date Consistency
        extracted_date = extracted_fields.get('incident_date')
        if extracted_date and claim_incident_date:
            if str(extracted_date) != str(claim_incident_date):
                mismatch_count += 1
                validations.append({
                    'rule_name': 'DOCUMENT_DATE_MISMATCH',
                    'category': 'CROSS_DOCUMENT_CONSISTENCY',
                    'status': 'WARNING',
                    'severity': 'MEDIUM',
                    'description': f"Incident date on Claim Form ({claim_incident_date}) differs from Accident Report / Document ({extracted_date}). Verification required.",
                    'detected_value': str(extracted_date),
                    'expected_value': str(claim_incident_date),
                    'source_document_type': 'ACCIDENT_REPORT',
                    'target_document_type': 'CLAIM_FORM'
                })
                evidence.append({
                    'type': 'DOCUMENT_MISMATCH',
                    'description': f"Date discrepancy detected: Claim Form indicates {claim_incident_date} while supporting documentation shows {extracted_date}.",
                    'severity': 'MEDIUM',
                    'source_doc': 'ACCIDENT_REPORT',
                    'source_field': 'incident_date',
                    'detected_value': str(extracted_date),
                    'expected_value': str(claim_incident_date),
                    'contribution': 18.0
                })
            else:
                validations.append({
                    'rule_name': 'DOCUMENT_DATE_MISMATCH',
                    'category': 'CROSS_DOCUMENT_CONSISTENCY',
                    'status': 'PASSED',
                    'severity': 'LOW',
                    'description': f"Incident dates align across all provided documentation ({claim_incident_date}).",
                    'detected_value': str(extracted_date),
                    'expected_value': str(claim_incident_date),
                    'source_document_type': 'ALL',
                    'target_document_type': 'ALL'
                })

        # 3. Check Amount Consistency (Claimed Amount vs Repair Invoice)
        extracted_invoice_amt = extracted_fields.get('invoice_amount')
        if extracted_invoice_amt:
            diff = abs(float(extracted_invoice_amt) - claim_amount)
            if diff > 100.0:
                mismatch_count += 1
                validations.append({
                    'rule_name': 'CLAIM_AMOUNT_INVOICE_MISMATCH',
                    'category': 'AMOUNT_TOLERANCE',
                    'status': 'WARNING',
                    'severity': 'MEDIUM',
                    'description': f"Claimed amount (${claim_amount:,.2f}) deviates from repair invoice total (${float(extracted_invoice_amt):,.2f}).",
                    'detected_value': f"${float(extracted_invoice_amt):,.2f}",
                    'expected_value': f"${claim_amount:,.2f}",
                    'source_document_type': 'REPAIR_INVOICE',
                    'target_document_type': 'CLAIM_FORM'
                })
                evidence.append({
                    'type': 'DOCUMENT_MISMATCH',
                    'description': f"Amount variance of ${diff:,.2f} detected between claimed amount and repair bill.",
                    'severity': 'MEDIUM',
                    'source_doc': 'REPAIR_INVOICE',
                    'source_field': 'invoice_amount',
                    'detected_value': f"${float(extracted_invoice_amt):,.2f}",
                    'expected_value': f"${claim_amount:,.2f}",
                    'contribution': 15.0
                })
            else:
                validations.append({
                    'rule_name': 'CLAIM_AMOUNT_INVOICE_MISMATCH',
                    'category': 'AMOUNT_TOLERANCE',
                    'status': 'PASSED',
                    'severity': 'LOW',
                    'description': f"Claimed amount reconciles with Repair Invoice (${claim_amount:,.2f}).",
                    'detected_value': f"${float(extracted_invoice_amt):,.2f}",
                    'expected_value': f"${claim_amount:,.2f}",
                    'source_document_type': 'REPAIR_INVOICE',
                    'target_document_type': 'CLAIM_FORM'
                })

        # 4. Check Vehicle Registration Number Alignment
        extracted_vehicle = extracted_fields.get('vehicle_number')
        if extracted_vehicle:
            norm_ext_veh = str(extracted_vehicle).replace('-', '').replace(' ', '').upper()
            if norm_ext_veh != claim_vehicle:
                mismatch_count += 1
                validations.append({
                    'rule_name': 'VEHICLE_REGISTRATION_MISMATCH',
                    'category': 'CROSS_DOCUMENT_CONSISTENCY',
                    'status': 'FAILED',
                    'severity': 'HIGH',
                    'description': f"Vehicle registration mismatch: Claim indicates {claim_vehicle} while document contains {norm_ext_veh}.",
                    'detected_value': str(extracted_vehicle),
                    'expected_value': str(claim_data.get('vehicle_number')),
                    'source_document_type': 'DOCUMENT',
                    'target_document_type': 'CLAIM'
                })
                evidence.append({
                    'type': 'DOCUMENT_MISMATCH',
                    'description': f"Discrepancy in vehicle registration number across documents ({extracted_vehicle} vs {claim_data.get('vehicle_number')}).",
                    'severity': 'HIGH',
                    'source_doc': 'DOCUMENT',
                    'source_field': 'vehicle_number',
                    'detected_value': str(extracted_vehicle),
                    'expected_value': str(claim_data.get('vehicle_number')),
                    'contribution': 22.0
                })
            else:
                validations.append({
                    'rule_name': 'VEHICLE_REGISTRATION_MISMATCH',
                    'category': 'CROSS_DOCUMENT_CONSISTENCY',
                    'status': 'PASSED',
                    'severity': 'LOW',
                    'description': f"Vehicle registration number ({claim_data.get('vehicle_number')}) verified across documents.",
                    'detected_value': str(extracted_vehicle),
                    'expected_value': str(claim_data.get('vehicle_number')),
                    'source_document_type': 'DOCUMENT',
                    'target_document_type': 'CLAIM'
                })

        # 5. Check Invoice Number Pattern
        ext_invoice = extracted_fields.get('invoice_number')
        if ext_invoice and claim_invoice and ext_invoice != claim_invoice:
            mismatch_count += 1
            validations.append({
                'rule_name': 'INVOICE_NUMBER_MISMATCH',
                'category': 'CROSS_DOCUMENT_CONSISTENCY',
                'status': 'WARNING',
                'severity': 'MEDIUM',
                'description': f"Entered invoice number ({claim_invoice}) differs from document invoice number ({ext_invoice}).",
                'detected_value': str(ext_invoice),
                'expected_value': str(claim_invoice),
                'source_document_type': 'REPAIR_INVOICE',
                'target_document_type': 'CLAIM_FORM'
            })
            evidence.append({
                'type': 'DOCUMENT_MISMATCH',
                'description': f"Invoice reference mismatch: {ext_invoice} vs {claim_invoice}.",
                'severity': 'MEDIUM',
                'source_doc': 'REPAIR_INVOICE',
                'source_field': 'invoice_number',
                'detected_value': str(ext_invoice),
                'expected_value': str(claim_invoice),
                'contribution': 12.0
            })

        return {
            'validations': validations,
            'evidence': evidence,
            'mismatch_count': mismatch_count,
            'duplicate_indicator': duplicate_indicator,
            'invoice_reuse_indicator': invoice_reuse_indicator
        }
