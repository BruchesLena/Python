import numpy as np
import sklearn.linear_model as lm

#df = np.loadtxt("D:\Regression\data_set.txt", delimiter = ' ')
#print df

x_data = np.loadtxt("D:\Regression\input.txt", delimiter = ' ')
y_data = np.loadtxt("D:\Regression\output.txt")


skm = lm.LogisticRegression(solver='lbfgs')
skm.fit(x_data, y_data)
prediction = skm.predict([3, 0])
print "Prediction"
print prediction
