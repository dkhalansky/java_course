set terminal png size 800,600
set output 'metric3.png'
plot "< paste d.txt '../Separate threads/metric3.txt'" title "Separate threads", "< paste d.txt '../Thread pool/metric3.txt'" title "Thread pool",  "< paste d.txt ../Nonblocking/metric3.txt" title "Nonblocking";
