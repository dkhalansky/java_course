set terminal png size 800,600
set output 'metric1.png'
plot "< paste n.txt '../Separate threads/metric1.txt'" title "Separate threads", "< paste n.txt '../Thread pool/metric1.txt'" title "Thread pool",  "< paste n.txt ../Nonblocking/metric1.txt" title "Nonblocking";
