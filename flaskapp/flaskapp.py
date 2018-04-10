from flask import Flask
import pickle
import numpy as np

import sklearn

print sklearn.__version__
print np.__version__

from sklearn.svm import LinearSVC
from sklearn.preprocessing import CategoricalEncoder

import atexit
import MySQLdb 

ALLOWED_EXTENSIONS = ["csv"]

app = Flask(__name__)

app.config['UPLOAD_FOLDER'] = "upload_folder"

clf = None

X_ce = [range(-1, 4) for i in xrange(9)]
X_ce = np.array(X_ce)
X_ce = X_ce.transpose()

ce = CategoricalEncoder()
ce.fit(X_ce)

settings_db = [None for i in xrange(6)]

with app.open_resource("settings.db", "r") as f_in:
	i = 0
	for line in f_in:
		settings_db[i] = line
		i += 1

db_driver = settings_db[0]
db_db = settings_db[1]
db_port = int(settings_db[2])
db_user = settings_db[3]
db_pass = settings_db[4]
db_host = settings_db[5]

db = MySQLdb.connect(host=db_host,
                     user=db_user,    
                     passwd=db_pass, 
                     db=db_db,
                     port=db_port)    
# don't forget db.close() at end of program

cur = db.cursor()

atexit.register(db.close)

def execute_print_query(qry):
	cur.execute(qry)

	# print all the first cell of all the rows
	for row in cur.fetchall():
	    print row[0]


def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

def train_decision_maker(file_instance, file_name):
	data_pd = pd.read_csv(file_instance)

	clf = LinearSVC()

	data_pd

	data = data_pd.as_matrix()

	X = data[:, 0:-1]
	y = data[:, -1]

	X_ce = [range(-1, 4) for i in xrange(9)]
	X_ce = np.array(X_ce)
	X_ce = X_ce.transpose()

	ce = CategoricalEncoder()
	ce.fit(X_ce)
	X = ce.transform(X).todense()

	clf.fit(X, y)

	clf.predict(X)

	with open(file_name + ".pickle", "w") as f_out:
	    pickle.dump(clf, f_out)

def load_decision_maker(file_name):
	with app.open_resource(file_name, "r") as f_in:
	    clf = pickle.load(f_in)

	    return clf

@app.route('/', methods=['GET', 'POST'])
def upload_file():
    if request.method == 'POST':
        # check if the post request has the file part
        if 'file' not in request.files:
            flash('No file part')
            return redirect(request.url)
        file = request.files['file']
        # if user does not select file, browser also
        # submit a empty part without filename
        if file.filename == '':
            flash('No selected file')
            return redirect(request.url)
        if file and allowed_file(file.filename):
            filename = secure_filename(file.filename)
            file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
            return redirect(url_for('uploaded_file',
                                    filename=filename))
    return '''
    <!doctype html>
    <title>Upload new File</title>
    <h1>Upload new File</h1>
    <form method=post enctype=multipart/form-data>
      <p><input type=file name=file>
         <input type=submit value=Upload>
    </form>
    '''

@app.route('/get_decision_behavior/<behavior_file_name>/<game_state>')
def get_decision(behavior_file_name, game_state):
    global clf
    try:
        if clf is None:
            clf = load_decision_maker(behavior_file_name)

            if clf is None:
                return "n/a"
            
        gs = [int(x) for x in game_state.split(",")]
        X = ce.transform(np.array(gs).reshape(1, -1)).todense()
        return ",".join([str(x) for x in clf.predict(X)])
    except Exception as e:
        return str(e)

@app.route('/')
def hello_world():
    return 'Hello, World! Welcome to CIS 400'

@app.route('/get_decision/<game_state>')
def get_decision(game_state):
    global clf
    try:
        if clf is None:
            with app.open_resource("pickle.rick", "r") as f_in:
                clf = pickle.load(f_in)

            if clf is None:
                return "n/a"
            
        gs = [int(x) for x in game_state.split(",")]
        X = ce.transform(np.array(gs).reshape(1, -1)).todense()
        return ",".join([str(x) for x in clf.predict(X)])
    except Exception as e:
        return str(e)
        

if __name__ == "__main__":
    
    with app.open_resource("pickle.rick", "r") as f_in:
       clf = pickle.load(f_in)

    print "Loaded clf", clf
       
    app.run()

