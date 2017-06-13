import numpy as np

if __name__ == '__main__':
    train_data = 1.8*np.ones((50,2))
    train_data = np.append(train_data, 0.7*np.ones((40,2)), 0)
    train_data = np.append(train_data, 2.0*np.ones((45,2)), 0)
    train_data[:,0] -= 0.6
    train_data[:,1] += 2.0
    train_data[:,0] /= np.max(train_data[:,0])
    train_data[:,1] /= 1.2*np.max(train_data[:,1])
    train_data[:,1] = 1 - train_data[:,1]
    train_data[90:,1] += 0.5

    test_data = np.array([train_data[0], train_data[89], train_data[134]])
    np.random.shuffle(test_data)
    test_data = np.insert(test_data, [0, 1, 2, 3], (0,0), 0)
    test_data = np.repeat(test_data, 20, 0)

    train_data += np.random.normal(0.0, 0.05, train_data.shape)
    test_data += np.random.normal(0.0, 0.02, test_data.shape)
    np.random.shuffle(train_data)

    np.savetxt('train.csv', train_data, delimiter=',')
    np.savetxt('test.csv', test_data, delimiter=',')
