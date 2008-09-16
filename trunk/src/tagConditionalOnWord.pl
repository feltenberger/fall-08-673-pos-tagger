#!/usr/bin/perl

open(INFILE,"../resources/wsj/combined.pos");
open(OUTFILE, "> ../resources/tagWordCounts.txt");


while($line = <INFILE>){
  next if /^\*x\*/;
  foreach $w (split(' ',$line)) {
    if ($w =~ /(.*)\/([^\/]+)$/) {
      	++$frequencies{$2}{$1};
    }
  }
}

foreach $tag (sort keys %frequencies) {
	
	$total = 0;
	foreach $word (sort keys %{ $frequencies{$tag}} ) {
 		$total += $frequencies{$tag}{$word};
 	}
 	foreach $word (sort keys %{ $frequencies{$tag}} ) {
 		$prob = $frequencies{$tag}{$word} / $total;
 		print OUTFILE "$tag , $word: $prob \n";
 	} 
}

