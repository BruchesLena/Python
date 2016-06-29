import numpy as np
import sklearn.linear_model as lm

#df = np.loadtxt("D:\Regression\data_set.txt", delimiter = ' ')
#print df

x_data = np.loadtxt("D:\Regression\input.txt", delimiter = ' ')
y_data = np.loadtxt("D:\Regression\output.txt")
test_data = np.loadtxt("D:/Regression/test_input.txt", delimiter = ' ')
#print test_data

skm = lm.LogisticRegression(solver='lbfgs')
skm.fit(x_data, y_data)

f = open('D:/Regression/our_output.txt', 'w')
for array in test_data:
    prediction = skm.predict(array)
    for element in prediction:
        #print element
        f.write(str(element) + '\n')
f.close()
