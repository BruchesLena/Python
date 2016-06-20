import numpy as np
import pandas as pd
import statsmodels.api as sm
import patsy as pt
import sklearn.linear_model as lm

# загружаем файл с данными
df = np.loadtxt("D:\Machine Learning\SentAnalysis\data_set.txt", delimiter = ' ')
print df
# x - таблица с исходными данными факторов (x1, x2, x3)
x = [[65, 0], [48, 0], [15, 1]]
# y - таблица с исходными данными зависимой переменной
y = [0, 0, 1]

# создаем пустую модель
skm = lm.LogisticRegression()
# запускаем расчет параметров для указанных данных
skm.fit(x, y)
# и выведем параметры рассчитанной модели
prediction = skm.predict([15,1])
print "Prediction"
print prediction
