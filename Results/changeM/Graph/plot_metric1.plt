set terminal png size 800,600
set output 'metric1.png'
plot "< paste m.txt '../Separate threads/metric1.txt'" title "Separate threads", "< paste m.txt '../Thread pool/metric1.txt'" title "Thread pool",  "< paste m.txt ../Nonblocking/metric1.txt" title "Nonblocking";
