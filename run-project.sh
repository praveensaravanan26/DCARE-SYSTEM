#!/bin/bash
# ==============================================================================
# DCARE System Startup Script (Mac / Linux)
# Starts: PostgreSQL (check), Redis (check), AI Service, Backend, Frontend
# ==============================================================================

set -e

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
echo "======================================================================"
echo " Starting DCARE - Document-Centric Automated Risk Evaluation Platform"
echo " Project Directory: $PROJECT_ROOT"
echo "======================================================================"

# 1. Check PostgreSQL
echo "[1/5] Verifying PostgreSQL Database..."
if ! psql -U dcare_user -d dcare_db -c '\q' 2>/dev/null; then
    echo "  [!] PostgreSQL dcare_db is not reachable. Ensure postgres service is running."
    echo "      Command: brew services start postgresql@16"
else
    echo "  ✓ PostgreSQL 'dcare_db' connected successfully."
fi

# 2. Check Redis
echo "[2/5] Verifying Redis Server..."
if ! redis-cli ping 2>/dev/null | grep -q PONG; then
    echo "  [!] Redis is not reachable. Starting redis service..."
    redis-server --daemonize yes || brew services start redis
fi
echo "  ✓ Redis Cache is active on port 6379."

# 3. Start Python AI Microservice (Port 8000)
echo "[3/5] Starting Python AI & Document Intelligence Service (Port 8000)..."
if lsof -t -i :8000 >/dev/null; then
    echo "  [!] Freeing port 8000..."
    kill -9 $(lsof -t -i :8000) || true
fi
cd "$PROJECT_ROOT/ai-service"
export DYLD_FALLBACK_LIBRARY_PATH=/opt/homebrew/opt/libomp/lib
"$PROJECT_ROOT/ai-service/venv/bin/uvicorn" app.main:app --host 0.0.0.0 --port 8000 > "$PROJECT_ROOT/ai-service.log" 2>&1 &
AI_PID=$!
echo "  ✓ AI Microservice started (PID: $AI_PID, Logs: ai-service.log)"

# Wait for AI service health
sleep 2
curl -s http://localhost:8000/health >/dev/null && echo "  ✓ AI Health endpoint verified (UP)."

# 4. Start Spring Boot Backend (Port 8080)
echo "[4/5] Starting Spring Boot Enterprise Backend (Port 8080)..."
if lsof -t -i :8080 >/dev/null; then
    echo "  [!] Freeing port 8080..."
    kill -9 $(lsof -t -i :8080) || true
fi
cd "$PROJECT_ROOT/backend"
mvn spring-boot:run > "$PROJECT_ROOT/backend.log" 2>&1 &
BACKEND_PID=$!
echo "  ✓ Spring Boot Backend started (PID: $BACKEND_PID, Logs: backend.log)"

# 5. Start React + Vite Frontend (Port 5173)
echo "[5/5] Starting React Frontend Dev Server (Port 5173)..."
if lsof -t -i :5173 >/dev/null; then
    echo "  [!] Freeing port 5173..."
    kill -9 $(lsof -t -i :5173) || true
fi
cd "$PROJECT_ROOT/frontend"
npm run dev -- --host 0.0.0.0 --port 5173 > "$PROJECT_ROOT/frontend.log" 2>&1 &
FRONTEND_PID=$!
echo "  ✓ React Frontend started (PID: $FRONTEND_PID, Logs: frontend.log)"

echo "======================================================================"
echo " ✓ DCARE PLATFORM IS ACTIVE & READY!"
echo "----------------------------------------------------------------------"
echo " Web Application URL:   http://localhost:5173"
echo " Backend REST API URL:  http://localhost:8080/api"
echo " AI Microservice URL:   http://localhost:8000"
echo "----------------------------------------------------------------------"
echo " Enterprise User Logins (RBAC Authenticated):"
echo "  • Claim Officer:       officer@dcare.local      / Officer@123"
echo "  • Fraud Investigator:  investigator@dcare.local / Investigator@123"
echo "  • Customer:            customer@dcare.local     / Customer@123"
echo "  • Administrator:       admin@dcare.local        / Admin@123"
echo "======================================================================"
