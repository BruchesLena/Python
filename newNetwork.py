import numpy as np
import cPickle
import gzip
import random

f = gzip.open("C:\Users\User\Documents\Python Scripts\mnist_pkl.gz", 'rb')
training_data, validation_data, test_data = cPickle.load(f)

def vectorized_result(j):
    e = np.zeros((10, 1))
    e[j] = 1.0
    return e

#training_inputs = [np.reshape(x, (784, 1)) for x in training_data[0]]
#training_results = [vectorized_result(y) for y in training_data[1]]
#training_data = zip(training_inputs, training_results)
#validation_inputs = [np.reshape(x, (784, 1)) for x in validation_data[0]]
#validation_data = zip(validation_inputs, validation_data[1])
#test_inputs = [np.reshape(x, (784, 1)) for x in test_data[0]]
#test_data = zip(test_inputs, test_data[1])

def __sigmoid(self, z):
    return 1.0 / (1.0 + np.exp(-z))
    
def sigmoid_prime(self, z):
    return self.sigmoid(z)*(1 - self.sigmoid(z))
    
class network:
    def __init__(self, sizes):
        self.num_layers = len(sizes)
        self.sizes = sizes
        self.biases = [np.random.randn(y, 1) for y in sizes [1:]]
        self.weights = [np.random.randn(x, y) for x, y in zip(sizes[:-1], sizes[1:])]
        
    def feedforward(self, a):
        for bias, weight in zip(self.biases, self.weights):
            a = self.sigmoid(np.dot(weight, a) + bias)
            

                
    def backprop(self, x):
        new_bias = [np.zeros(b.shape) for b in self.biases]
        new_weights = [np.zeros(w.shape) for w in self.weights]
        activation = x
        activations = [x]
        zs = list()
        for b, w in zip(new_bias, new_weights):
            z = np.dot(activation, w) + b
            zs.append(z)
            activation = self.sigmoid(z)
            activations.append(activation)
        delta = self.cost_derivative(activations[-1], x) * self.sigmoid_prime(zs[-1])
        new_bias[-1] = delta
        new_weights[-1] = np.dot(delta, activations[-2].transpose())
        for l in range(2, self.num_layers):
             z = zs[-l]
             sp = self.sigmoid_prime(z)
             delta = np.dot(self.weights[-l + 1].transpose(), delta) * sp
             new_bias[-l] = delta
             new_weights[-l] = np.dot(delta, activations[-l - 1].transpose())
        return (new_bias, new_weights)
                
    def update_mini_batch(self, mini_batch, eta):
        new_bias = [np.zeros(b.shape) for b in self.biases]
        new_weights = [np.zeros(w.shape) for w in self.weights]
        for x in mini_batch:
            delta_new_bias, delta_new_weights = self.backprop(x)
            new_bias = [nb + dnb for nb, dnb in zip(new_bias, delta_new_bias)]
            new_weights = [nw + dnw for nw, dnw in zip(new_weights, delta_new_weights)]
        self.weights = [w - (eta / len(mini_batch)) * nw for w, nw in zip(self.weights, new_weights)]
        self.biases = [b - (eta / len(mini_batch)) * nb for b, nb in zip(self.biases, new_bias)] 
        
    def SGD(self, training_data, epochs, mini_batch_size, eta, test_data = None):
        for j in range(epochs):
            mini_batches = [training_data[k:k+mini_batch_size] for k in range(0, len(training_data), mini_batch_size)]
            for mini_batch in mini_batches:
                self.update_mini_batch(mini_batch, eta)
            if test_data:
                print("Epoch {0}: {1} / {2}".format(j, self.evaluate(test_data), len(test_data)))
            else:
                print("Epoch {0} complete".format(j))

        
    def evaluate(self, test_data):
        test_results = [(np.argmax(self.feedforward(x)), y) for (x, y) in test_data]
        return sum(int(x == y) for (x, y) in test_results)
        
    def cost_derivative(self, output_activations, y):
         return (output_activations-y)
         
nn = network([784, 100, 10])
nn.SGD(training_data, 25, 10, 0.1, test_data)