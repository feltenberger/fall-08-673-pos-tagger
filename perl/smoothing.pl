#!/usr/bin/perl
use Statistics::LineFit;

open( INFILE, "../resources/tag_word_count.dat" );

sub log2 {
	my $num = $_[0];
	if ( $num == 0 ) { return 0; }
	else { return log($num) / log(2); }
}

while ( $line = <INFILE> ) {
	@words = split( /\s/, $line );
	$tag = $words[0];
	$count{$tag}{ $words[2] }++;
	$count{$tag}{'total'} += $words[2];
}
close (INFILE);

foreach $tag ( sort keys %count ) {
	foreach $wordCount (sort keys %{$count{$tag}})
	{
		$prob{$tag}[$wordCount] = $wordCount / $count{$tag}{'total'};
	}
	
	@y = (
		$count{$tag}{'1'}, $count{$tag}{'2'}, $count{$tag}{'3'},
		$count{$tag}{'4'}, $count{$tag}{'5'}, $count{$tag}{'6'}
	);
	@x = ( 1 ... 6 );
	$yCount = 0;
	$checkTwoPoints = 1;
	foreach $ys (@y)
	{
		if (ord $ys != 0)
		{
		$yCount++;
		}
	}
	
	if ($yCount < 2)
	{
		@keyCounts = sort {$a <=> $b} keys %{$count{$tag}};
		if (defined $keyCounts[2])
		{
			push @y, $count{$tag}{$keyCounts[2]};
			push @x, $keyCounts[2];
		}
		else
		{
			print "$tag $keyCounts[1] $count{$tag}{'total'}\n";
			if ($keyCounts[1] < 5)
			{
				$checkTwoPoints = 0;
				$prob{$tag}[$keyCounts[1]] = $keyCounts[1] / ($count{$tag}{'total'} + 1);
				$prob{$tag}[0] = $prob{$tag}[$keyCounts[1]];
				print "HERE $tag $prob{$tag}[0] \n";
			}
			else
			{
				$prob{$tag}[0] = 0;
			}

		} 
	}
	if ( $checkTwoPoints != 0)
	 {
		@yVal = map {log2($_)} @y;
		
		@xVal = map {log2($_)} @x;
		$lineFit = Statistics::LineFit->new();
	
		$lineFit->setData( \@xVal, \@yVal ) or die "Invalid data";
		( $intercept, $slope ) = $lineFit->coefficients();
		@predictedYs = $lineFit->predictedYs();

		foreach $j (0...5)
		{
			$newNc[$j] = 2 ** $predictedYs[$j];
		}
		
		foreach $i ( 0 ... 4 ) {
			$prob{$tag}[ $i + 1 ] =
			  ( ( $i + 1 ) * ( $newNc[ $i + 1 ] / $newNc[$i] ) ) /
			  $count{$tag}{'total'};

		}
		$prob{$tag}[0] = $count{$tag}{'1'} / $count{$tag}{'total'};
	}

}

open( INFILE, "../resources/tag_word_count.dat");
open( PROBS, " > ../resources/tag_word_prob.dat");

while ( $line = <INFILE> ) {
	@words = split( /\s/, $line );
	if (!defined $seen{$words[0]})
	{
		print PROBS "$words[0]\t<DEFAULT>\t$prob{$words[0]}[0]\n";
		$seen{$words[0]} = 1;
	}
	print PROBS "$words[0]\t$words[1]\t$prob{$words[0]}[$words[2]]\n" 
}

