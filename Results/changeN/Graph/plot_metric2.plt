set terminal png size 800,600
set output 'metric2.png'
plot "< paste n.txt '../Separate threads/metric2.txt'" title "Separate threads", "< paste n.txt '../Thread pool/metric2.txt'" title "Thread pool",  "< paste n.txt ../Nonblocking/metric2.txt" title "Nonblocking";
