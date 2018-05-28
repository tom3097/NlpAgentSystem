from nltk.parse import stanford

parser = stanford.StanfordParser()
tree = next(parser.raw_parse('It clearly have a problem with ghost touches that are appearing on the lower right part of the screen.'))

try:
    import tkinter
    tree.draw()
except ImportError:
    print(tree)