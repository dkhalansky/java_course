set terminal png size 800,600
set output 'metric1.png'
plot "< paste d.txt '../Separate threads/metric1.txt'" title "Separate threads", "< paste d.txt '../Thread pool/metric1.txt'" title "Thread pool",  "< paste d.txt ../Nonblocking/metric1.txt" title "Nonblocking";
