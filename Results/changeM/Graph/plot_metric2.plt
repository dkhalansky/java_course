set terminal png size 800,600
set output 'metric2.png'
plot "< paste m.txt '../Separate threads/metric2.txt'" title "Separate threads", "< paste m.txt '../Thread pool/metric2.txt'" title "Thread pool",  "< paste m.txt ../Nonblocking/metric2.txt" title "Nonblocking";
