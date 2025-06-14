"""Main entrypoint for backend-ai application."""
import os
import uvicorn
from backend_ai.api import app

def start():
    host = os.getenv("HOST", "0.0.0.0")
    port = int(os.getenv("PORT", 8000))
    uvicorn.run(app, host=host, port=port)
