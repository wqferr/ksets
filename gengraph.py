from matplotlib import pyplot as plt
from scipy import genfromtxt
import sys

if __name__ == '__main__':
    color = iter(['b-', 'g-', 'r-', 'k-'])
    handles = []
    for dat in sys.argv[1:]:
        out_dat = genfromtxt(dat, delimiter=',')
        g, = plt.plot(out_dat, next(color), label=dat)
        handles.append(g)

    plt.legend(handles=handles)
    plt.show()
