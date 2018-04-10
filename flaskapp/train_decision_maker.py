import pickle
import pandas as pd
from sklearn.preprocessing import CategoricalEncoder, OneHotEncoder
from sklearn.svm import LinearSVC
import numpy as np
from sklearn.pipeline import Pipeline

file_name = "sample.csv"

data_pd = pd.read_csv(file_name)

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

with open("classifier.pickle", "w") as f_out:
    pickle.dump(clf, f_out)



