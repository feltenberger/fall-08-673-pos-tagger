#!/usr/bin/perl

$end = -1;  # -1 means:  1. just started parsing no ./. found
$previous_tag[0] = "<s>";
$pp_tag = "";
$p_tag  = "";

while (<>)
{
  $previous_tag[1] = "<s>";
  foreach $w (split(' ')) {

    if ($w =~ /(.*)\/(.*)/) {

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
      		
      		}
      		@previous_tag = ();
      		$previous_tag[0] = $1;	
      		$previous_tag[1] = $2;	
      		$previous_tag[2] = $3;	
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

        
    }
  }
}

##### the number of occurances of $previous_tag followed by $tag C(Tj,Tk) #####
for $tag_prev ( keys %tags_matrix ) {
  for $tag ( keys %{ $tags_matrix{$tag_prev} } ) {
    printf("%s\t%s\t%d\n", $tag_prev, $tag, $tags_matrix{$tag_prev}{$tag});
  }
}

