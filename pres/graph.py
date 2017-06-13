import numpy as np
import sys

from matplotlib import pyplot as plt


if __name__ == '__main__':
    lines = ['o', '-']
    for filename, t in zip(sys.argv[1:], lines):
        data = np.loadtxt(filename, delimiter=',')
        if len(data.shape) == 1:
            plt.plot(data, t, label='{}'.format(filename))
        else:
            for i, col in enumerate(data.T):
                plt.plot(col, t, label='{}[{}]'.format(filename, i))

    plt.legend()
    plt.show()
