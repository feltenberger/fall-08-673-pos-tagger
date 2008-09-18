#!/usr/bin/perl

open(INFILE,"../resources/wsj/combined.pos");
open(OUTFILE, "> ../resources/tagWordProb.txt");
open(COUNTFILE, "> ../resources/tagWordCounts.txt");

while($line = <INFILE>){
  next if /^\*x\*/;
  foreach $w (split(' ',$line)) {
    if ($w =~ /(.*)\/([^\/]+)$/) {
      	++$frequencies{$2}{$1};
      	if (!defined $words{$1})
      	{
      		$words{$1} = 1;
      	}
    }
  }
}

foreach $tag (sort keys %frequencies) {
	
	$total = 0;
	foreach $word (sort keys %{ $frequencies{$tag}} ) {
 		$total += $frequencies{$tag}{$word};
 	}
 	
 	foreach $word (sort keys %words) {
 		if (defined $frequencies{$tag}{$word})
 		{
	 		$prob = $frequencies{$tag}{$word} / $total;
 			print OUTFILE "$tag\t$word\t$frequencies{$tag}{$word}\n";
 			print COUNTFILE "$word\n";
 		}
 	} 
}