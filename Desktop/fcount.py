fname = raw_input("filename:");
with open(fname) as f:
    content = f.readlines()
    count = 0
    for line in content:
    	if line == '0.0\n':
    		count+=1
    print count;