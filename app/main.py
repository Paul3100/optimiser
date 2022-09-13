import flask
from flask import Flask, request, jsonify, send_file

app = Flask(__name__)
import json
import sqlite3
from PIL import Image, ImageEnhance
import base64

@app.route("/authentication", methods=['POST'])
def auth():
    details = authenticate()
    details.initiate()
    details.logging()
    # Create Dictionary - Need to return json object to prevent error on client side
    value = {
        "Status": details.verdict,
    }
    # Dictionary to JSON Object using dumps() method
    # Return JSON Object
    return json.dumps(value)


@app.route("/adding", methods=['POST'])
def auth_up():
    details = authenticate()
    details.initiate()
    details.signup()
    # Create Dictionary - Need to return json object to prevent error on client side
    value = {
        "Status": details.verdict,
    }
    # Dictionary to JSON Object using dumps() method
    # Return JSON Object
    return json.dumps(value)


@app.route("/optimise", methods=['POST'])
def optimise():
    data = request.form['bytes']
    print(data[0:50])
    decodeit = open('convert.jpeg', 'wb')
    decodeit.write(base64.b64decode(data))
    decodeit.close()

    details = optimal()
    details.compress()

    value = {
        "bytes": details.converted_string,
    }

    return json.dumps(value)

class optimal:
    def __init__(self):
        self.img = Image.open("convert.jpeg")
        self.filter = ImageEnhance.Contrast(self.img)
        self.im_output = self.filter.enhance(1.1)
        self.im_output.save('convert.jpeg')
        self.im = Image.open("convert.jpeg")
        self.enhancer = ImageEnhance.Sharpness(self.im)
        self.im_s_1 = self.enhancer.enhance(6)
        self.im_s_1.save('convert.jpeg');
    def compress(self):
        with open("convert.jpeg", "rb") as image:
            self.converted_string = image.read()
            self.converted_string = str(base64.b64encode(self.converted_string))
            self.converted_string = self.converted_string[2:len(self.converted_string) - 1]

        # 25% reduction in size -- Lossless compression

class authenticate:
    def __init__(self):
        self.verdict = ""
        self.content = request.json
        self.username = self.content['username']
        self.password = self.content['password']
        self.initiate()

    def initiate(self):
        self.con = sqlite3.connect('data.db')
        self.cur = self.con.cursor()
        # Is user already registered
        self.get = self.cur.execute("select * from users where username = \'{}\'".format(self.username))
        self.get = self.get.fetchall()

    def logging(self):
        # If username exists
        if (len(self.get) == 1):
            # Verifying password
            if (self.get[0][1] == self.password):
                self.verdict = "Successful"
            else:
                self.verdict = "Details may be incorrect/Account may not exist"

        else:  # Sets JSON body text
            self.verdict = "Details may be incorrect/Account may not exist"

        self.con.commit()
        self.con.close()

    def signup(self):
        if (len(self.get) == 1):
            self.verdict = "Account exists already - log in"
        else:
            self.get = self.cur.execute("insert into users values (\'{}\',\'{}\')".format(self.username, self.password))
            self.verdict = "Successful"

        self.con.commit()
        self.con.close()


if __name__ == '__main__':
    app.debug = True
    app.run(host='0.0.0.0', port=8000)
