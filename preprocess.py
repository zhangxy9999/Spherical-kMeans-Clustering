import os
import re
import csv
from collections import Counter

if not os.path.exists('./purifiedTexts'):
    os.makedirs('purifiedTexts')

def purify(fname, content):
    f = open('./purifiedTexts/' + fname + '.txt', 'w')
    loadingLines = False
    for line in content:
        if loadingLines:
            f.write(getAU(line))
        else:
            if 'Subject:' in line:
                f.write(getAU(re.sub(r'Subject:', '', line)))
            if 'Lines:' in line:
                loadingLines = True
    f.close()

def getAU(input):
    return (re.sub('[\W_]', ' ', input.lower()))


for root, dirs, files in os.walk('./20_newsgroups/'):
    for name in files:
        if not name[0] == '.':
            path = root + '/' + name
            c = open(path, 'r', encoding='cp437')
            alllines = c.readlines()
            c.close()
            needRemove = False
            for line in alllines:
                if 'Subject:' and 'Re:' in line:
                    needRemove = True
                if 'Subject:' and 're:' in line:
                    needRemove = False
            if needRemove:
                os.remove(path)
            else:
                purify(root[16:] + '_' + name, alllines)

bagwriter = csv.writer(open('bag.csv', 'a'))
ngclassfile = csv.writer(open('newsgroups.class.csv', 'w'))
rlablefile = csv.writer(open('newsgroups.rlable.csv', 'w'))
i = 0
for root, dirs, files in os.walk('./purifiedTexts/'):
    for name in files:
        if not name[0] == '.':
            i = i + 1
            #objnum = name.split('_')[1][:-4]
            classname = name.split('_')[0]
            ngclassfile.writerow([i, classname])
            rlablefile.writerow([i, name[:-4]])
            path = root + name
            c = open(path, 'r')
            content = c.readline()
            c.close()
            if content != '':
                words = filter(lambda x: ((x != '') and (not x.isdigit())), content.split(' '))
                c = open(path, 'w')
                for word in words:
                    c.write(word + ' ')
                c.close()
                words2 = filter(lambda x: ((x != '') and (not x.isdigit())), content.split(' '))
                freqs = Counter(words2)
                for key, value in freqs.items():
                    bagwriter.writerow([i, key, value])

def getNGram(n):
    i = 0
    writer = csv.writer(open('char' + str(n) + '.csv', 'a'))
    for root, dirs, files in os.walk('./purifiedTexts/'):
        for name in files:
            if not name[0] == '.':
                i = i + 1
                #objnum = name.split('_')[1][:-4]
                path = root + name
                c = open(path, 'r')
                content = c.readline()
                c.close()
                i = 0
                l = len(content)
                rlist = []
                while i+n-1 < l:
                    rlist.append(content[i:i+n])
                    i = i+1
                freqs = Counter(rlist)
                for key, value in freqs.items():
                    writer.writerow([i,key,value])


getNGram(3);
getNGram(5);
getNGram(7);




