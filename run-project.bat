@echo off
REM ==============================================================================
REM DCARE System Startup Script (Windows)
REM ==============================================================================

echo ======================================================================
echo  Starting DCARE - Document-Centric Automated Risk Evaluation Platform
echo ======================================================================

set PROJECT_ROOT=%~dp0

echo [1/3] Starting Python AI Microservice (Port 8000)...
start "DCARE AI Service" cmd /k "cd /d %PROJECT_ROOT%ai-service && venv\Scripts\activate && uvicorn app.main:app --host 0.0.0.0 --port 8000"

echo [2/3] Starting Spring Boot Backend (Port 8080)...
start "DCARE Backend" cmd /k "cd /d %PROJECT_ROOT%backend && mvn spring-boot:run"

echo [3/3] Starting React Frontend (Port 5173)...
start "DCARE Frontend" cmd /k "cd /d %PROJECT_ROOT%frontend && npm run dev"

echo ======================================================================
echo  All services launched in separate terminal windows.
echo  Access Web Application: http://localhost:5173
echo ======================================================================
