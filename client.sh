#!/bin/bash
for i in {1..2}
do
	#echo 'Test'
	java cs455.scale.client.Client st-vrain 7077 10 > $i'.log'
done