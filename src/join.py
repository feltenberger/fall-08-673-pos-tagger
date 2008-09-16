#! /usr/bin/python

import os

#base directory for all the files
dir = "../resources/wsj/"
out = open("../resources/wsj/combined.pos","w")

files = os.listdir(dir)

#descend into subdirectories
for file in files:
    #ignore non subdirectories such as combined file
    if os.path.isdir(os.path.join(dir,file)) == True:
        subFiles = os.listdir(os.path.join(dir,file))
        for subFile in subFiles:
            fullPath = os.path.join(dir,file,subFile)
            #ignore .svn and other random directories
            if os.path.isdir(fullPath) == False:
                print fullPath
                f = open(fullPath,"r")
                out.write(f.read())
                out.write("\n=======================\n")
                f.close()

out.close()
