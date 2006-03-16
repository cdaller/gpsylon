.SUFFIXES:	.dat .clear .csv .sql


## SwissMap 100
DB = Db
## SwissMap 50
#DB = Db2



#############################################
FILE	=  swissnames.csv
DAT	=  LnLT.dat
CLEAR	=  LnLT.clear
SQL = location.sql

all:  $(SQL)

$(DAT):
	unshield -g $(DB) x -d tmp /cdrom/data1.cab


$(CLEAR): $(DAT) 
	./dexor.pl tmp/$(DB)/$(DAT) > tmp/$(CLEAR)


$(FILE): $(CLEAR)
	./makecsv.pl tmp/$(CLEAR) > $(FILE)
	
$(SQL): $(FILE)
	./gen-location.pl


decrypt: $(CLEAR)
extract: $(DAT)
csv: $(FILE)
sql: $(SQL)

clean:
	rm -rf *~ *.*~ tmp

distclean:
	rm -rf *~ *.*~ tmp *.csv *.sql

