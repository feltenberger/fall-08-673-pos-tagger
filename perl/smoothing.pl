#!/usr/bin/perl
use Statistics::LineFit;

open( INFILE, "../resources/start_tags_count.dat" );

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
		print "TAG $tag WORD: $wordCount\n";
		$prob{$tag}[$wordCount] = $wordCount / $count{$tag}{'total'};
	}
	@y = (
		$count{$tag}{'1'}, $count{$tag}{'2'}, $count{$tag}{'3'},
		$count{$tag}{'4'}, $count{$tag}{'5'}, $count{$tag}{'6'}
	);
	@x = ( 1 ... 6 );

	if (   $y[0] == 0
		&& $y[1] == 0
		&& $y[2] == 0
		&& $y[3] == 0
		&& $y[4] == 0
		&& $y[5] == 0 )
	{
		$prob{$tag}[0] = 0;
	}
	else {
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
		$t = 2 ** (11.74);
		
		foreach $i ( 0 ... 4 ) {
			$prob{$tag}[ $i + 1 ] =
			  ( ( $i + 1 ) * ( $newNc[ $i + 1 ] / $newNc[$i] ) ) /
			  $count{$tag}{'total'};
			print "$tag $i " . $prob{$tag}[ $i + 1 ] . "\n";

		}
	}
	if ( $newNc[0] > 1 ) {
		$prob{$tag}[0] = $count{$tag}{'1'} / $count{$tag}{'total'};
	}
	else {
		$prob{$tag}[0] = 0;
	}
	print "$tag Default: $count{$tag}{'0'}\n";
}

open( INFILE, "../resources/start_tags_count.dat");
open( PROBS, " > ../resources/start_tags_prob.txt");

while ( $line = <INFILE> ) {
	@words = split( /\s/, $line );
	if (!defined $seen{$words[0]})
	{
		print PROBS "$words[0]\t<DEFAULT>\t$prob{$words[0]}[0]\n";
		$seen{$words[0]} = 1;
	}
	print PROBS "$words[0]\t$words[1]\t$prob{$words[0]}[$words[2]]\n" 
}

