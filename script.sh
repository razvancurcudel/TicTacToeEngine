#You will need cygwin for this
#Replace bots paths you yours paths

#RUN ./script.sh noOfTests

tests=$(($1 / 2))

yourBot=C:\\Users\\vKint\\Desktop\\bot1.bat
AIBot=C:\\Users\\vKint\\Desktop\\bot2.bat

you1=0
opp1=0
d1=0

you2=0
opp2=0
d2=0

youG=0
oppG=0
dG=0


for i in `seq 1 $tests`;
    do
var=$(java -cp "E:\java projects\TicTacToeEngine\bin" tictactoe.TicTacToe "$yourBot" "$AIBot" 2>dump.txt)
winner=$(echo "$var" | tr -d '\r')  #delete \r character
if [ $winner -eq 1 ]
then 
	you1=$((you1 + 1))
	printf "WIN\n"
elif [ $winner -eq 2 ]
then 
	opp1=$((opp1 + 1))
	printf "LOSE\n"
else
	d1=$((d1 + 1))
	printf "DRAW\n"
fi
done

youG=$you1
oppG=$opp1
dG=$d1

printf "%s\n" "-----"

for i in `seq 1 $tests`;
    do
var=$(java -cp "E:\java projects\TicTacToeEngine\bin" tictactoe.TicTacToe "$AIBot" "$yourBot" 2>dump.txt)
winner=$(echo "$var" | tr -d '\r')
if [ $winner -eq 1 ]
then 
	opp2=$((opp2 + 1))
	printf "LOSE\n"
elif [ $winner -eq 2 ]
then 
	you2=$((you2 + 1))
	printf "WIN\n"
else
	d2=$((d2 + 1))
	printf "DRAW\n"
fi
done

oppG=$((oppG + $opp2))
youG=$((youG + $you2))
dG=$((dG + $d2))

printf "\n      FIRST \n"
printf "%s\n" "------------------"
printf "|   WINS:    %d   |\n" "$you1"
printf "|   LOSES:   %d   |\n" "$opp1"
printf "|   Draws:   %d   |\n" "$d1"
printf "%s\n" "------------------"

printf "\n      SECOND \n"
printf "%s\n" "------------------"
printf "|   WINS:    %d   |\n" "$you2"
printf "|   LOSES:   %d   |\n" "$opp2"
printf "|   Draws:   %d   |\n" "$d2"
printf "%s\n" "------------------"


printf "\n      GLOBAL \n"
printf "%s\n" "------------------"
printf "|   WINS:    %d   |\n" "$youG"
printf "|   LOSES:   %d   |\n" "$oppG"
printf "|   Draws:   %d   |\n" "$dG"
printf "%s\n" "------------------"