import os

FILE_NAME = "makefile"
SRC_DIR = "c_src/"
BIN_DIR_PREFIX = " ../c_bin/"
VAR_NAME = "OBJ"
SRC_EXT = ".c"
OBJ_EXT = ".o"

def update(line):
	line = VAR_NAME + " ="
	files = os.listdir(SRC_DIR)
	print("Nalezené moduly v adresáři zdrojových souborů:")
	for f in files:
		if f.endswith(SRC_EXT):
			print(f);
			line += BIN_DIR_PREFIX + f.replace(SRC_EXT, OBJ_EXT)
	line += "\n"
	print()
	return line

print("Spuštěn skript pro aktualizaci makefile.")
f = open(SRC_DIR + FILE_NAME, "r")
print("Otevřen makefile pro čtení.")
lines = f.readlines()
f.close()
print("Čtení dokončeno.\n")

n = len(lines)
for i in range(0, n):
	if (lines[i].startswith(VAR_NAME)):
		print("Nalezen řádek s názvy objektových souborů:\n", lines[i])
		lines[i] = update(lines[i])
		print("Řádek po aktualizaci:\n", lines[i])

print("Otevřen makefile pro zápis.")
f = open(SRC_DIR + FILE_NAME, "w")
f.writelines(lines)
f.close()
print("Zápis dokončen.\n")