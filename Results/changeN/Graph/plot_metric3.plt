set terminal png size 800,600
set output 'metric3.png'
plot "< paste n.txt '../Separate threads/metric3.txt'" title "Separate threads", "< paste n.txt '../Thread pool/metric3.txt'" title "Thread pool",  "< paste n.txt ../Nonblocking/metric3.txt" title "Nonblocking";
