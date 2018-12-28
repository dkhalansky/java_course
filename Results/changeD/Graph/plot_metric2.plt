set terminal png size 800,600
set output 'metric2.png'
plot "< paste d.txt '../Separate threads/metric2.txt'" title "Separate threads", "< paste d.txt '../Thread pool/metric2.txt'" title "Thread pool",  "< paste d.txt ../Nonblocking/metric2.txt" title "Nonblocking";
