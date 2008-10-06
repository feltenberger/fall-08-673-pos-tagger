#!/usr/bin/perl
use Getopt::Std;
getopt('io', \%opts);

if(defined $opts{'i'})
{
$inFile = $opts{'i'};
open(INFILE, $inFile);	
}
else
{
open(INFILE,"../resources/wsj/90training6.pos");
}
if(defined $opts{'o'})
{
$probsFile = $opts{'o'};
open( OUTFILE, " > $probsFile");
}
else
{
open( OUTFILE, " > ../resources/$inFile_count.dat");
}

$totalWords= 0; 
while($line = <INFILE>){
  next if /^\*x\*/;
  foreach $w (split(' ',$line)) {
    if ($w =~ /(.*)\/([^\/]+)$/) {
      	$word = $1;
      	$tag = $2;
      	if($tag =~ /(\w{2,3})\|(\w{2,3})\|(\w{2,3})/)
      	{
      		$frequencies{$1}{$word}++;
      		$frequencies{$2}{$word}++;
      		$frequencies{$3}{$word}++;
      	}
      	elsif($tag =~ /(\w{2,3})\|(\w{2,3})/)
      	{
      		$frequencies{$1}{$word}++;
      		$frequencies{$2}{$word}++;
      	}
      	else
      	{
      		$frequencies{$tag}{$word}++;
      	}
      	if (!defined $words{$word})
      	{
      		$words{$word} = 1;
      		$totalWords++;
      	}
      
    }
  }
}

print "$totalWords";
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
 		}
 	} 
}

close(OUTFILE);