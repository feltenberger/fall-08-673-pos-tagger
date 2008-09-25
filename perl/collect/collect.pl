#!/usr/bin/perl


use Time::HiRes;
$secStart = Time::HiRes::time;


#===================================================================

$end = -1;  # -1 means:  1. just started parsing no ./. found
$previous_tag[0] = "<s>";
$pp_tag = "";
$p_tag  = "";

while (<>)
{
  $previous_tag[1] = "<s>";
  foreach $w (split(' ')) {

    if ($w =~ /(.*)\/(.*)/) {

      ##### number of occurances of tags that start a sentence #####
      if (($end)||($end == -1)) {
        $start_tags_hash{$2}++;
        $end = 0;
        $start_tags++;
      }
      if ($w eq "./.") { $end = 1; }

      
      ##### the number of occurances of $previous_tag followed by $tag C(Tj,Tk) #####
      $tag = $2;
      if($tag =~ /(\w{2,3})\|(\w{2,3})\|(\w{2,3})/)
      	{
      		foreach $p_tag (@previous_tag)
      		{
      			if($p_tag ne "")
      			{
      			$tags_matrix{$p_tag}{$1}++;
      			$tags_matrix{$p_tag}{$2}++;
      			$tags_matrix{$p_tag}{$3}++;
      			}
      			else
      			{
      				print "$p_tag $1 $2 $3\n";
      			}
      		}
      		@previous_tag = ();
      		$previous_tag[0] = $1;	
      		$previous_tag[1] = $2;	
      		$previous_tag[2] = $3;	
      		print "THREE: $1 $2 $3\n";
      	}
      	elsif($tag =~ /(\w{2,3})\|(\w{2,3})/)
      	{
      		foreach $p_tag (@previous_tag)
      		{
      			$tags_matrix{$p_tag}{$1}++;
      			$tags_matrix{$p_tag}{$2}++;
      		}
      		@previous_tag = ();
      		$previous_tag[0] = $1;	
      		$previous_tag[1] = $2;	
      	}
      	else
      	{
      		foreach $p_tag (@previous_tag)
      		{
      			$tags_matrix{$p_tag}{$tag}++;
      		}
      		@previous_tag = ();
            $previous_tag[0] = $tag;
      	}

      ##### the number of occurances of $previous_tag followed by $tag C(Tj,Tk-1, Tk-2) #####
      if ($pp_tag ne "") {
	if ($p_tag ne "") {
	  $pp_tags_matrix{$pp_tag}{$p_tag}{$2}++;
	  $pp_tag = $p_tag;
	  $p_tag = $2;
	} else {
	  $p_tag = $2;
	}
      } else {
	$pp_tag = $2;
      }
        
      ##### the number of occurances of $tag in corpus C(Tj) #####
      $tags{$tag}++;


      ##### the number of occurances of $words that are tagged as $tag #####
      $words{$1}{$2}++;
      
     
      ###### the number of occurances of
      $elements++;
    }
  }
}

#====================== OUTPUT ==============================================
$outf1="start_tags_count.dat";
$outf2="start_tag_prevtag.dat";
$outf3="word_tag_count.dat";
$outf4="number_of_elements.dat";
$outf5="pptag_ptag_tag.dat";

if (-e $outf1) { unlink($outf1) or die "ERROR: could not delete file $outf1 - $!\n"; }
if (-e $outf2) { unlink($outf2) or die "ERROR: could not delete file $outf2 - $!\n"; }
if (-e $outf3) { unlink($outf3) or die "ERROR: could not delete file $outf3 - $!\n"; }
if (-e $outf4) { unlink($outf4) or die "ERROR: could not delete file $outf4 - $!\n"; }
if (-e $outf5) { unlink($outf5) or die "ERROR: could not delete file $outf5 - $!\n"; }

open OUTF_START_TAGS_COUNT,  	">$outf1" or die "ERROR: could not open file $outf1 - $!\n";
open OUTF_TAG_PREVTAG_COUNT, 	">$outf2" or die "ERROR: could not open file $outf2 - $!\n";
open OUTF_WORD_TAG_COUNT,    	">$outf3" or die "ERROR: could not open file $outf3 - $!\n";
open OUTF_GENERAL_STATS,     	">$outf4" or die "ERROR: could not open file $outf4 - $!\n";
open OUTF_PPTAG_PTAG_TAG_COUNT, ">$outf5" or die "ERROR: could not open file $outf5 - $!\n";


##### number of occurances of elements in corpus #####
printf(OUTF_GENERAL_STATS "Elements\t%d\n", $elements);
printf(OUTF_GENERAL_STATS "Tags\t%d\n", (scalar keys %tags));
##### number of occurances of tags that start a sentence #####
for $tag (keys %start_tags_hash) {
  printf(OUTF_START_TAGS_COUNT "<s>\t%s\t%s\n", $tag, $start_tags_hash{$tag});
}

##### the number of occurances of $previous_tag followed by $tag C(Tj,Tk) #####
for $tag_prev ( keys %tags_matrix ) {
  for $tag ( keys %{ $tags_matrix{$tag_prev} } ) {
    printf(OUTF_TAG_PREVTAG_COUNT "%s\t%s\t%d\n", $tag_prev, $tag, $tags_matrix{$tag_prev}{$tag});
  }
}


##### the number of occurances of $previous_tag followed by $tag C(Tj,Tk) #####
my $ref_to_pptm = \%pp_tags_matrix;

for my $tag_prev_prev ( keys %$ref_to_pptm ) {
  for my $tag_prev ( keys %{$ref_to_pptm->{$tag_prev_prev}} ) {
    for my $tag ( keys %{$ref_to_pptm->{ $tag_prev_prev }->{ $tag_prev }} ) { 
      printf(OUTF_PPTAG_PTAG_TAG_COUNT "%s\t%s\t%s\t%d\n", $tag_prev_prev, $tag_prev, $tag, $ref_to_pptm->{$tag_prev_prev}->{$tag_prev}->{$tag});
    }
  }
}

##### the number of occurances of $words that are tagged as $tag #####
for $word ( keys %words ) {
  for $tag ( keys %{ $words{$word} } ) {
    printf(OUTF_WORD_TAG_COUNT "%s\t%s\t%d\n" , $tag, $word, $words{$word}{$tag});
  }
}



printf("Elapsed time: %f sec.\n", (Time::HiRes::time - $secStart));