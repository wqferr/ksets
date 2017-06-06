from matplotlib import pyplot as plt
from scipy import genfromtxt
import sys

# plots 1 2d function
if __name__ == '__main__':
    color = iter(['b-', 'r-', 'g-'])
    out_dat = genfromtxt(sys.argv[1], delimiter=',')
    plt.plot(out_dat[:,0], next(color))
    plt.plot(out_dat[:,1], next(color))
    plt.show()
