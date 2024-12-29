import base64
import google.generativeai as genai
import sys


genai.configure(api_key=sys.argv[1])
model = genai.GenerativeModel("gemini-1.5-flash")
doc_path = sys.argv[2].replace("%20", " ") # Replace with the actual path to your local PDF

# Read and encode the local file
with open(doc_path, "rb") as doc_file:
    doc_data = base64.standard_b64encode(doc_file.read()).decode("utf-8")

prompt = " ".join(sys.argv[3:])
response = model.generate_content([{'mime_type': 'application/pdf', 'data': doc_data}, prompt])

print(response.text)