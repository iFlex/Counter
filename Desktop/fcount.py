fname = raw_input("filename:");
with open(fname) as f:
    content = f.readlines()
    srt = [];
    for line in content:
    	srt.append(float(line[0:-3]))
    srt.sort();
    print srt