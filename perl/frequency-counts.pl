#!/usr/bin/perl
#
while(<>){
  next if /^\*x\*/;

  foreach $w (split(' ')) {
    if ($w =~ /\/([^\/]+)$/) {
      ++$frequencies{$1};
    }
  }
}

foreach $tag (sort keys %frequencies) {
  print "$tag: $frequencies{$tag}\n";
}


