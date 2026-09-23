#!/usr/bin/env python3
"""
DCARE High-Fidelity Synthetic Document Generator (ReportLab PDF)
Generates native vector PDFs with exact document intelligence fields for:
- Scenario 1: Clean claim (Straight-Through Processing candidate)
- Scenario 2: Date & Amount Cross-Document Mismatch
- Scenario 3: High Frequency / High Amount Outlier Claim
- Scenario 4: Reused Duplicate Invoice
"""

import os
from reportlab.lib.pagesizes import letter
from reportlab.lib import colors
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, HRFlowable
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle

DOCS_DIR = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "datasets", "demo_documents")
os.makedirs(DOCS_DIR, exist_ok=True)

def build_pdf(filename, title, subtitle, metadata_rows, statement=None):
    filepath = os.path.join(DOCS_DIR, filename)
    doc = SimpleDocTemplate(
        filepath,
        pagesize=letter,
        rightMargin=40,
        leftMargin=40,
        topMargin=40,
        bottomMargin=40
    )
    
    styles = getSampleStyleSheet()
    header_style = ParagraphStyle(
        'HeaderStyle',
        parent=styles['Heading1'],
        fontName='Helvetica-Bold',
        fontSize=18,
        textColor=colors.HexColor('#0f172a'),
        spaceAfter=4
    )
    sub_style = ParagraphStyle(
        'SubStyle',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=10,
        textColor=colors.HexColor('#475569'),
        spaceAfter=15
    )
    cell_label_style = ParagraphStyle(
        'CellLabel',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=9,
        textColor=colors.HexColor('#334155')
    )
    cell_val_style = ParagraphStyle(
        'CellVal',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=9,
        textColor=colors.HexColor('#0f172a')
    )
    body_style = ParagraphStyle(
        'Body',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=9,
        leading=14,
        textColor=colors.HexColor('#334155')
    )

    elements = []
    
    # Header Banner
    elements.append(Paragraph("DCARE ENTERPRISE CLAIMS NETWORK", ParagraphStyle('Net', fontName='Helvetica-Bold', fontSize=10, textColor=colors.HexColor('#2563eb'))))
    elements.append(Paragraph(title, header_style))
    elements.append(Paragraph(subtitle, sub_style))
    elements.append(HRFlowable(width="100%", thickness=1, color=colors.HexColor('#cbd5e1'), spaceAfter=15))
    
    # Table Content
    table_data = []
    for row in metadata_rows:
        table_data.append([
            Paragraph(f"<b>{row[0]}:</b>", cell_label_style),
            Paragraph(str(row[1]), cell_val_style)
        ])
        
    t = Table(table_data, colWidths=[180, 340])
    t.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, -1), colors.HexColor('#f8fafc')),
        ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
        ('PADDING', (0, 0), (-1, -1), 6),
        ('LINEBELOW', (0, 0), (-1, -1), 0.5, colors.HexColor('#e2e8f0')),
    ]))
    elements.append(t)
    elements.append(Spacer(1, 15))
    
    # Statement Section
    if statement:
        elements.append(Paragraph("<b>Official Statement / Remarks:</b>", cell_label_style))
        elements.append(Spacer(1, 4))
        stmt_table = Table([[Paragraph(statement, body_style)]], colWidths=[520])
        stmt_table.setStyle(TableStyle([
            ('BACKGROUND', (0, 0), (-1, -1), colors.HexColor('#f1f5f9')),
            ('PADDING', (0, 0), (-1, -1), 10),
            ('BOX', (0, 0), (-1, -1), 0.5, colors.HexColor('#cbd5e1')),
        ]))
        elements.append(stmt_table)
        elements.append(Spacer(1, 15))
        
    elements.append(HRFlowable(width="100%", thickness=0.5, color=colors.HexColor('#e2e8f0'), spaceBefore=15, spaceAfter=8))
    elements.append(Paragraph("CONFIDENTIAL & PRIVILEGED - Controlled Synthetic Benchmark Data for Automated Risk Evaluation Platform.", ParagraphStyle('Foot', fontName='Helvetica-Oblique', fontSize=7.5, textColor=colors.HexColor('#94a3b8'))))
    
    doc.build(elements)
    print(f"[+] Generated PDF: {filepath}")

def generate_all():
    # Scenario 1: Clean / Consistent Normal Claim
    build_pdf("scenario1_claim_form.pdf",
              "MOTOR CLAIM NOTIFICATION FORM",
              "Official Initial Notice of Loss (FNOL) Registration",
              [
                  ("Claim Number", "CLM-2026-1001"),
                  ("Policy Number", "POL-99281"),
                  ("Policyholder Name", "Johnathon Doe"),
                  ("Vehicle Registration No", "MH-02-CB-4091"),
                  ("Incident Date", "2026-08-10"),
                  ("Incident Location", "Outer Ring Road, Mumbai"),
                  ("Claimed Amount", "$18500.00"),
                  ("Workshop / Garage", "Apex Auto Care Center"),
                  ("Invoice Number", "INV-2026-891")
              ],
              "Driver vehicle stopped at traffic signal when rear-ended by trailing car at low speed. Rear bumper assembly damaged.")

    build_pdf("scenario1_accident_report.pdf",
              "ACCIDENT INCIDENT REPORT",
              "First Information & Officer Spot Verification Record",
              [
                  ("Report Number", "FIR-9928-MUM"),
                  ("Policy Number", "POL-99281"),
                  ("Vehicle Registration No", "MH-02-CB-4091"),
                  ("Incident Date", "2026-08-10"),
                  ("Incident Location", "Outer Ring Road, Mumbai"),
                  ("Reporting Officer", "Inspector R. Sharma"),
                  ("Driver Insured", "Johnathon Doe")
              ],
              "Officer attended scene. Confirmed rear contact with MH-02-CB-4091. No injuries reported.")

    build_pdf("scenario1_repair_invoice.pdf",
              "TAX INVOICE & REPAIR SUMMARY",
              "Authorized Garage Final Billing and Parts Itemization",
              [
                  ("Invoice Number", "INV-2026-891"),
                  ("Invoice Date", "2026-08-11"),
                  ("Garage Name", "Apex Auto Care Center"),
                  ("Vehicle Registration No", "MH-02-CB-4091"),
                  ("Parts Replacement Total", "$12300.00"),
                  ("Labor & Painting Charges", "$6200.00"),
                  ("Total Amount", "$18500.00")
              ],
              "Replaced rear bumper beam, repainted trunk panel. Quality inspection cleared.")

    # Scenario 2: Cross-Document Discrepancies
    build_pdf("scenario2_claim_form.pdf",
              "MOTOR CLAIM NOTIFICATION FORM",
              "Official Initial Notice of Loss (FNOL) Registration",
              [
                  ("Claim Number", "CLM-2026-2002"),
                  ("Policy Number", "POL-99281"),
                  ("Policyholder Name", "Johnathon Doe"),
                  ("Vehicle Registration No", "MH-02-CB-4091"),
                  ("Incident Date", "2026-08-12"),
                  ("Incident Location", "Western Express Highway"),
                  ("Claimed Amount", "$42000.00"),
                  ("Workshop / Garage", "QuickFix Garage"),
                  ("Invoice Number", "INV-2026-902")
              ],
              "Side collision on expressway during rain.")

    build_pdf("scenario2_accident_report_mismatch.pdf",
              "ACCIDENT INCIDENT REPORT",
              "First Information & Officer Spot Verification Record",
              [
                  ("Report Number", "FIR-9944-MUM"),
                  ("Policy Number", "POL-99281"),
                  ("Vehicle Registration No", "MH-02-CB-4091"),
                  ("Incident Date", "2026-08-14"), # Mismatch with Claim Form (Aug 14 vs Aug 12)
                  ("Incident Location", "Western Express Highway"),
                  ("Reporting Officer", "Inspector K. Varma")
              ],
              "Patrol vehicle noted incident on August 14th evening.")

    build_pdf("scenario2_repair_invoice_mismatch.pdf",
              "TAX INVOICE & REPAIR SUMMARY",
              "Authorized Garage Final Billing and Parts Itemization",
              [
                  ("Invoice Number", "INV-2026-902"),
                  ("Invoice Date", "2026-08-15"),
                  ("Garage Name", "QuickFix Garage"),
                  ("Vehicle Registration No", "MH-02-CB-4091"),
                  ("Parts Replacement Total", "$35000.00"),
                  ("Labor Charges", "$14500.00"),
                  ("Total Amount", "$49500.00") # Mismatch with Claim Form ($49500 vs $42000)
              ],
              "Chassis alignment, quarter panel replacement.")

    # Scenario 3: Suspicious High Velocity / Outlier Claim
    build_pdf("scenario3_claim_form_highfreq.pdf",
              "MOTOR CLAIM NOTIFICATION FORM",
              "Official Initial Notice of Loss (FNOL) Registration",
              [
                  ("Claim Number", "CLM-2026-3003"),
                  ("Policy Number", "POL-88314"),
                  ("Policyholder Name", "Johnathon Doe"),
                  ("Vehicle Registration No", "KA-01-MJ-8822"),
                  ("Incident Date", "2026-08-20"),
                  ("Incident Location", "Electronic City Flyover"),
                  ("Claimed Amount", "$128000.00"), # Extreme statistical outlier
                  ("Workshop / Garage", "Apex Auto Care Center"),
                  ("Invoice Number", "INV-2026-940")
              ],
              "High-speed front collision causing severe engine compartment damage.")

    # Scenario 4: Duplicate Invoice Reused
    build_pdf("scenario4_duplicate_invoice.pdf",
              "TAX INVOICE & REPAIR SUMMARY",
              "Authorized Garage Final Billing and Parts Itemization",
              [
                  ("Invoice Number", "INV-2026-891"), # Duplicate invoice # previously used in Scenario 1
                  ("Invoice Date", "2026-08-25"),
                  ("Garage Name", "Apex Auto Care Center"),
                  ("Vehicle Registration No", "MH-02-CB-4091"),
                  ("Total Amount", "$18500.00")
              ],
              "Billing for bumper panel replacement.")

if __name__ == '__main__':
    generate_all()
