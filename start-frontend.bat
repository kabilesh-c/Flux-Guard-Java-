@echo off
echo ========================================
echo  Starting Fraud Detection Frontend
echo ========================================
echo.

cd frontend

if not exist node_modules (
    echo Installing dependencies...
    echo This will take 2-3 minutes...
    echo.
    call npm install
    echo.
    echo Dependencies installed!
    echo.
)

echo Starting Vite development server...
echo.
echo Frontend will be available at: http://localhost:5173
echo.

call npm run dev
