from flask import Flask

app = Flask(__name__)

@app.route('/hello')
def hello():
    return {
        "mesage": "Hello World from Flask application!"
    }

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0')
