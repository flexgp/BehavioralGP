import numpy as np
from sklearn.tree import DecisionTreeRegressor
import matplotlib.pyplot as plt
import sys

args = sys.argv
number_of_features = int(args[1])
data = args[2:]
for i in xrange(len(data)):
    data[i] = float(data[i])

X = []
Y = []

for dataPointIndex in xrange(len(data)/(number_of_features + 1)):
    dataPoint = []
    for featureIndex in xrange(number_of_features):
        index = dataPointIndex*(number_of_features + 1) + featureIndex
        dataPoint.append(data[index])
    X.append(dataPoint)
    targetValue = data[dataPointIndex*(number_of_features + 1) + number_of_features]
    Y.append(targetValue)

clf = DecisionTreeRegressor()
clf.fit(X, Y)

error = 1.0 - 1.0/(1.0 + np.sum(clf.predict(X) - np.asarray(Y)))
complexity = 1.0 - 1.0/clf.tree_.node_count
features = []
for i in xrange(len(clf.feature_importances_)):
    if clf.feature_importances_[i] != 0:
        features.append(1)
    else:
        features.append(0)

print error
print complexity
for feature in features:
    print feature
