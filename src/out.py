from classes import *
def main():

    i = 0
    hasIterated = False
    print("A block")

    while (i < 5):
        if not hasIterated:
            hasIterated = True
        else:
            print("A block")

        print("B block")
        i = i+1

if __name__ == "__main__":
    main()