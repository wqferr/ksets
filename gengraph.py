from matplotlib import pyplot as plt
from scipy import genfromtxt
import sys

# plots N 1d functions
if __name__ == '__main__':
    color = iter(['b-', 'r-', 'g-'])
    for dat in sys.argv[1:]:
        out_dat = genfromtxt(dat, delimiter=',')
        plt.plot(out_dat, next(color))
    plt.show()
