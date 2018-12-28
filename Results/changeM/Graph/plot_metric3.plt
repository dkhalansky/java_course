set terminal png size 800,600
set output 'metric3.png'
plot "< paste m.txt '../Separate threads/metric3.txt'" title "Separate threads", "< paste m.txt '../Thread pool/metric3.txt'" title "Thread pool",  "< paste m.txt ../Nonblocking/metric3.txt" title "Nonblocking";
