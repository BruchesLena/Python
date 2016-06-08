# -*- coding: utf-8 -*-
"""
Created on Thu May 05 22:25:45 2016

@author: User
"""

"""
mnist_loader
~~~~~~~~~~~~

A library to load the MNIST image data.  For details of the data
structures that are returned, see the doc strings for ``load_data``
and ``load_data_wrapper``.  In practice, ``load_data_wrapper`` is the
function usually called by our neural network code.
"""

#### Libraries
# Standard library
import cPickle
import gzip
import random

# Third-party libraries
import numpy as np

def load_data():
    """Return the MNIST data as a tuple containing the training data,
    the validation data, and the test data.

    The ``training_data`` is returned as a tuple with two entries.
    The first entry contains the actual training images.  This is a
    numpy ndarray with 50,000 entries.  Each entry is, in turn, a
    numpy ndarray with 784 values, representing the 28 * 28 = 784
    pixels in a single MNIST image.

    The second entry in the ``training_data`` tuple is a numpy ndarray
    containing 50,000 entries.  Those entries are just the digit
    values (0...9) for the corresponding images contained in the first
    entry of the tuple.

    The ``validation_data`` and ``test_data`` are similar, except
    each contains only 10,000 images.

    This is a nice data format, but for use in neural networks it's
    helpful to modify the format of the ``training_data`` a little.
    That's done in the wrapper function ``load_data_wrapper()``, see
    below.
    """
    f = gzip.open("C:\Users\User\Documents\Python Scripts\mnist_pkl.gz", 'rb')
    training_data, validation_data, test_data = cPickle.load(f)
    f.close()
    return (training_data, validation_data, test_data)

def load_data_wrapper():
    """Return a tuple containing ``(training_data, validation_data,
    test_data)``. Based on ``load_data``, but the format is more
    convenient for use in our implementation of neural networks.

    In particular, ``training_data`` is a list containing 50,000
    2-tuples ``(x, y)``.  ``x`` is a 784-dimensional numpy.ndarray
    containing the input image.  ``y`` is a 10-dimensional
    numpy.ndarray representing the unit vector corresponding to the
    correct digit for ``x``.

    ``validation_data`` and ``test_data`` are lists containing 10,000
    2-tuples ``(x, y)``.  In each case, ``x`` is a 784-dimensional
    numpy.ndarry containing the input image, and ``y`` is the
    corresponding classification, i.e., the digit values (integers)
    corresponding to ``x``.

    Obviously, this means we're using slightly different formats for
    the training data and the validation / test data.  These formats
    turn out to be the most convenient for use in our neural network
    code."""
    tr_d, va_d, te_d = load_data()
    training_inputs = [np.reshape(x, (784, 1)) for x in tr_d[0]]
    training_results = [vectorized_result(y) for y in tr_d[1]]
    training_data = zip(training_inputs, training_results)
    validation_inputs = [np.reshape(x, (784, 1)) for x in va_d[0]]
    validation_data = zip(validation_inputs, va_d[1])
    test_inputs = [np.reshape(x, (784, 1)) for x in te_d[0]]
    test_data = zip(test_inputs, te_d[1])
    return (list(training_data), list(validation_data), list(test_data))

def vectorized_result(j):
    """Return a 10-dimensional unit vector with a 1.0 in the jth
    position and zeroes elsewhere.  This is used to convert a digit
    (0...9) into a corresponding desired output from the neural
    network."""
    e = np.zeros((10, 1))
    e[j] = 1.0
    return e


class Network(object):

    def __init__(self, sizes):
        """
        The list ``sizes`` contains the number of neurons in the respective layers of the network.
        For example, if the list was [2, 3, 1] then it would be a three-layer network, with the first layer containing
        2 neurons, the second layer 3 neurons, and the third layer 1 neuron. Индекс маасива - номер слоя.

        The biases and weights for the network are initialized randomly, using a Gaussian distribution with mean 0,
        and variance 1.

        Например: size = [2, 3, 1]
        * [np.random.randn(y, 1) for y in sizes[1:]]
           np.random.randn() - Return a sample (or samples) from the “standard normal” distribution.
           (y, 1) for y in sizes[1:] - [(3, 1), (1, 1)]
                        (3 нейрона)             (1 нейрон)
            => [array([[ 1.07525546],
                       [ 0.57338746],  array([[-0.71175361]])]
                       [-0.73410565]]),

        * [np.random.randn(y, x) for x, y in zip(sizes[:-1], sizes[1:])]
            list(zip(size[:-1], size[1:])) => [(2, 3), (3, 1)]
                  [1 (2 нейрона) - 2 (3 нейрона)]      [2 (3 нейрона) - 3 (1 нейрон)]
            [array([[-1.05200768,  0.14314311],
                    [ 0.52989804, -1.18860133],   array([[-0.6810671 , -1.08362344,  0.72229427]])]
                    [-1.89307825, -0.7838967 ]]),
        """
        self.num_layers = len(sizes)
        self.sizes = sizes
        self.biases = [np.random.randn(y, 1) for y in sizes[1:]]
        self.weights = [np.random.randn(y, x) for x, y in zip(sizes[:-1], sizes[1:])]

    def __sigmoid(self, z):
        """
        The sigmoid function Одна из возможных функций активации.
        Переменная Z.

        aji - input activation (j-слой, i-номер нейрона);
        THETAj - матрица весов от слоя j к слою j+1;
        Пример: a12 = g(THETA1_10 * X0 + THETA1_11 * X1 + ...); + bias

        Z2 = THET1 * a1
        a2 = g(Z2)
                           То есть, мы берем веса от предыдущего слоя к текущему * активацию нейронов предыдущего слоя;
        Z3 = THET2 * a2             Получаем активацию нейронов текущего слоя;
        a3 = g(Z3)

        сигмоида - g(...)
        """
        return 1.0 / (1.0 + np.exp(-z))

    def sigmoid_prime(self, z):
        """
        Derivative of the sigmoid function. Производная от целевой функции. В данном случае от сигмоиды.
        Необходима для алгоритма обратного распространения ошибки.
        """
        return self.sigmoid(z) * (1 - self.sigmoid(z))

    def feedforward(self, a):
        """
        Return the output of the network if ``a`` is input.
        Алгоритм прямого распространения.

        Аргумент: результат активации предыдущего слоя нейронной сети.
        Результат

                  b                                                             w
        [array([[-0.14877072],                            [array([[-0.04895646, -0.23310937],   array([[ 1.35913542,
                [-0.48624419],  array([[-0.97742067]])]           [ 0.0458885 , -1.66712799],           -1.2862489,
                [ 1.14379203]]),                                  [-1.3024894 ,  0.35921608]]),         -1.26460679]])]

                                        zip(b, w) - соединяем по индексу подмассивы.

        [[-0.04895646, -0.23310937],   [[a11],   [[-0.14877072]
         [ 0.0458885 , -1.66712799], *  [a12], +  [-0.48624419] = а2
         [-1.3024894 ,  0.35921608]]    [a13]]    [ 1.14379203]]

         [[ 1.35913542, * [[a21], [a22], [a23]] +  [[-0.97742067]] = a3
            -1.2862489,
            -1.26460679]]
        """
        for bias, weight in zip(self.biases, self.weights):
            a = self.sigmoid(np.dot(weight, a) + bias)
        return a

    def SGD(self, training_data, epochs, mini_batch_size, eta, test_data):
        """
        Для тренировки нейронной сети с помощью using mini-batch Stochastic Gradient Descent.

        training_data - [(x, y), (x, y)]
        test_data - если есть, то после каждой эпохи происходит оценка.
        eta - шаг обучения (the learning rate);

        """
        if test_data:
            n_test = len(test_data)

        n = len(training_data)
        for j in range(epochs):
            # В каждой эпохе:
            # перемешивание training_data
           # random.shuffle(training_data)
            # разбивка training_data на партии: диапазон от 0 до количества объектов в training_data c заданным шагом;
            # range(0, 20, 5) => [0, 5, 10, 15]
            mini_batches = [training_data[k:k+mini_batch_size] for k in range(0, n, mini_batch_size)]

            for mini_batch in mini_batches:
                # для каждой партии обновление весов и bias;
                self.update_mini_batch(mini_batch, eta)

            # оценка результата, если есть тестовые данные;
            if test_data:
                print("Epoch {0}: {1} / {2}".format(j, self.evaluate(test_data), n_test))
            else:
                print("Epoch {0} complete".format(j))

    def update_mini_batch(self, mini_batch, eta):
        """
        Обновляем веса и значения bias с помощью алгоритма градиентного спуска для каждого пакета тренировочных данных.
        """
        # инициализируем новые массивы весов и bias - копии старых, но 0 вместо значений;
        new_bias = [np.zeros(b.shape) for b in self.biases]
        new_weights = [np.zeros(w.shape) for w in self.weights]

        for x, y in mini_batch:
            # Для каждого (x, y) вызывается алгоритм обратного распространения ошибки.
            # Результат разница между новыми и старыми весами и bias.
            delta_new_bias, delta_new_weights = self.backprop(x, y)

            # На каждом шаге мы обновялем веса и bias суммируя текущие для очередной пары (x, y) и ранее записанные
            # в массивы new_bias, new_weights; Для формулы градиентного спуска.
            new_bias = [nb + dnb for nb, dnb in zip(new_bias, delta_new_bias)]
            new_weights = [nw + dnw for nw, dnw in zip(new_weights, delta_new_weights)]

        # Gradient descent:
        # старый вес - (шаг обучения / колич. эл. в пакете) * новый вес
        self.weights = [w - (eta / len(mini_batch)) * nw for w, nw in zip(self.weights, new_weights)]
        self.biases = [b - (eta / len(mini_batch)) * nb for b, nb in zip(self.biases, new_bias)]

    def backprop(self, x, y):
        """
        Return a tuple ``(nabla_b, nabla_w)`` representing the
        gradient for the cost function C_x.  ``nabla_b`` and
        ``nabla_w`` are layer-by-layer lists of numpy arrays, similar
        to ``self.biases`` and ``self.weights``."""

        # инициализируем новые массивы весов и bias - копии старых, но 0 вместо значений;
        new_bias = [np.zeros(b.shape) for b in self.biases]
        new_weights = [np.zeros(w.shape) for w in self.weights]

        ############################ Feedforward algorithm. ##############################
        # Z2 = THET1 * a1
        # a2 = g(Z2)

        #           W                      X             b
        # [[-0.04895646, -0.23310937],             [[-0.14877072]
        # [0.0458885, -1.66712799],     *  X    +  [-0.48624419]
        # [-1.3024894, 0.35921608]]                [1.14379203]]

        # [[1.35913542, * а1  + [[-0.97742067]]
        #  - 1.2862489,
        # -1.26460679]]

        activation = x
        # list to store all the activations (а), layer by layer.
        activations = [x]
        # list to store all the z vectors, layer by layer.
        zs = list()

        # Сначала  activation = input.
        # Потом переменная activation переопределяется после каждой пары W, b.
        # То есть, для каждого x из (x, y) будет последовательность activations = [x, a1, a2, a3, ..., an]
        for b, w in zip(self.biases, self.weights):
            z = np.dot(w, activation) + b
            zs.append(z)
            activation = self.sigmoid(z)
            activations.append(activation)

        ############################ Backward pass algorithm. ##############################
        # Помним, что дельта для последнего слоя и для остальных считается по-разному.
        # Считаем дельту для последнего слоя: a (последнего слоя) - y (из (x, y)) * производ. сигмоиды от z (посл. сл.)
        delta = self.cost_derivative(activations[-1], y) * self.sigmoid_prime(zs[-1])
        # Обновляем bias для последнего слоя.
        new_bias[-1] = delta
        # Обновляем веса для последнего слоя: дельта * а (предпоследнего слоя).Т
        new_weights[-1] = np.dot(delta, activations[-2].transpose())

        # Расчитываем дельту для каждого слоя кроме последнего. Идем с конца.
        for l in range(2, self.num_layers):
            # если слой 2, то берем z с индексом -2.
            z = zs[-l]
            # считаем производную сигмоиды от z.
            sp = self.sigmoid_prime(z)
            # дельта = (веса предыдущего слоя (идем с конца, т.е. -2 + 1 = -1)).Т * дельту предыдущего слоя (сначала это
            # последний слой, потом на каждом цикле переназначаем переменную delta) * производную сигмоиды от z.
            delta = np.dot(self.weights[-l + 1].transpose(), delta) * sp
            # в массиве записываем bias для слоя;
            new_bias[-l] = delta
            # в массиве записываем новые веса для слоя; дельта * activations последующего
            new_weights[-l] = np.dot(delta, activations[-l - 1].transpose())
        return (new_bias, new_weights)

    def evaluate(self, test_data):
        """Return the number of test inputs for which the neural network outputs the correct result. Note that the neural
        network's output is assumed to be the index of whichever neuron in the final layer has the highest activation.
        """

        # Финальный прогон на последних весах и bias.
        test_results = [(np.argmax(self.feedforward(x)), y) for (x, y) in test_data]
        return sum(int(x == y) for (x, y) in test_results)

    def cost_derivative(self, output_activations, y):
        """Return the vector of partial derivatives \partial C_x \partial a for the output activations."""
        return (output_activations-y)

