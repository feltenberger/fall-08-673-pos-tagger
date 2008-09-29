#include <iostream>
#include <string>
#include <vector>
#include <fstream>
#include <cstdlib>

#include "LinkedList2.h"

vector<string>* split( string str, char splitBy )
{
	vector<string>* tags = new vector<string>;
	int i = 0;
	
	int pos = str.find('|');
	while ( pos > 0 && pos < (int)str.size() )
	{
		string temp = "";
		
		for ( i = 0; i < pos; i ++ )
		{
			temp += str[i];
		}
		tags->push_back(temp);

		str = str.substr( pos+1, str.size() );
		pos = str.find('|');
	}
	
	tags->push_back(str);
	
	return tags;
}

int main ()
{
	// OPEN FILE
	cout << "\tBEGIN" << endl;
	ifstream file_in("combined.pos");
	if (file_in.fail())
	{
		cout << "Input file opening failed." << endl << endl;
		exit(1);
	}
	cout << "\tFile Opened" << endl;
	
	cout << " Please enter if a tri-gram or a bi-gram " << endl;
	cout << "     (it must be a '2' or a '3' )       : "; 
	int size = 0;
	cin >> size;	
	
	// INITIALIZE VARIABLES
	int sentNum = 0,  
			k = 0, 
			pos = 0,
			j = 0,
			l = 0,
			m = 0;
	LinkedList* list = new LinkedList(size);
	vector<string> tags;
	vector<string>* tag1;
	vector<string>* tag2;
	vector<string>* tag3;
	string line = "", tag = "";
	bool test = false;


	cout << " Finding Probabilities for " << size << "-gram" << endl << endl;
	
	// READ THE FILE AND INSERT TO LINKED LIST 2
	file_in >> line;
	while ( !file_in.eof() )
	{
		pos = -1;
		tag = "";

		// GET THE FINAL STRING WITH THE /TAG
		pos = line.find('/');
//		cout << "got first '/' " << endl;
//		cout << " pos: " << pos << "\t line size: " << line.size() << endl;


		
//		cout << " got out of while loop " << endl;
		// ADD TAG TO SENTENCE VECTOR
		if ( pos > 0 && pos < (int)line.size() )
		{
			while (  pos > 0 && line.at(pos-1) == 92  && pos < (int)line.size())  
			{
//				cout << " pos: " << pos << "\t line size: " << line.size() << endl;
				line = line.substr( pos + 1, line.size() );
			
				pos = line.find('/');
			}
		
			tag = line.substr(pos + 1, line.size() );
			//cout << tag << " ";
			tags.push_back( tag );	
		}

		// INSERT SENTENCE TAGS TO LINKED LIST 2 AND THEN CLEAR THE SENTENCE VECTOR
		if (  tag == "." )
		{
			// COUNT SENTENCES
			if ( (sentNum % 10000 ) == 0 && (sentNum/10000) >= 0)
			{
				cout << " Sentences Imported:\t" << sentNum << endl;
			}

			//cout << endl << endl;
			sentNum++;
			
			if ( tags.size() == 0 )
			{
				cout << "tags vector is empty at end of sentence. " << endl;
				break;
			}

			if ( size == 2)
			{
				// INSERT TAGS TO LINKEDLIST 2 AND CLEAR VECTOR	
				for ( j = 0; j < (int)tags.size(); j++ )
				{
					tag1 = split( tags.at(j), '|' );
	
					if ( (j-1) >= 0 )
					{
						tag2 = split( tags.at(j-1), '|' );
					}
					else 
					{
						tag2 = new vector<string>;
						tag2->push_back("-");
					}
	
					for ( l = 0; l < (int)tag1->size(); l++)
					{
						for ( k = 0; k < (int)tag2->size(); k++ )
						{
							string* data = new string[size];
							data[0] = tag1->at(l);
							data[1] = tag2->at(k);

							list->insert( data ); 		
						}
					}
					
					delete tag1;
					tag1 = NULL; 
					delete tag2;
					tag2 = NULL;
				}
				
				tags.clear();
			}
			else if ( size == 3 )
			{
				// INSERT TAGS TO LINKEDLIST 2 AND CLEAR VECTOR	
				for ( j = 0; j < (int)tags.size(); j++ )
				{
					tag1 = split( tags.at(j), '|' );
	
					if ( (j-2) >= 0 )
					{
						tag2 = split( tags.at(j-1), '|' );
						tag3 = split( tags.at(j-2), '|' );
					}
					else if ( (j-1) >= 0 )
					{
						tag2 = split( tags.at(j-1), '|' );
	
						tag3 = new vector<string>;
						tag3->push_back("-");
					}
					else 
					{
						tag2 = new vector<string>;
						tag2->push_back("-");
						
						tag3 = new vector<string>;
						tag3->push_back("-");
					}
	
					for ( m = 0; m < (int)tag1->size(); m++)
					{
						for ( l = 0; l < (int)tag2->size(); l++)
						{
							for ( k = 0; k < (int)tag3->size(); k++ )
							{
								string* data = new string[size];
								data[0] = tag1->at(m);
								data[1] = tag2->at(l);
								data[2] = tag3->at(k);

								list->insert( data ); 		
							}
						}	
					}
					// may want to use delete
					tag1->clear();
					tag2->clear();
					tag3->clear();
				}
				
				tags.clear();
			}
			
		}
	
		file_in >> line;
	}
	
	
	cout << "\n\n Final Completed Sentences :\t" << sentNum << endl << endl; 
	test = list->write();
	cout << "File Written : " << test << endl << endl;
	
	cout << " STATISTICS" << endl;
	cout << "Total number of occurencs (N) : " << list->getTotalOccur() << endl;
	cout << "Total number of tag sequences : " << list->getTypes() << endl;
	
	cout << "\n\tCOMPLETED \n" ;

   return 0;
}
