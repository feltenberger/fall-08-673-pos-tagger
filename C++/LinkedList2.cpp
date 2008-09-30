
#include "LinkedList2.h"


/*********************************************************************
 *        NODE CLASS
 *********************************************************************/

// Default constructor
Node::Node()
   :next(NULL), tags(NULL),  size(-1), count(-1)
{
   // does nothing
}

/****************************************************************************
 * METHOD: Constructor
 * DESCRIPTION: Creates the node for the linked list
 * PARAMETERS: 
 * p_tags is a pointer to the string array of tags.  This must be dynamically
 *        allocated or it will cause bad output.
 * p_count is the number of times this tag sequence occured.
 * p_size is the number of tags stored in the array.
 ***************************************************************************/
Node::Node( string* p_tags, int p_size, int p_count )
   :next(NULL), tags(p_tags), size(p_size), count(p_count) 
{
   // does nothing
}


void Node::countpp()
{
   count++;
}

/**************
 * set methods
 *************/
void Node::setNext( Node* node )
{
   next = node;
}

/***************
 * get methods
 **************/
string* Node::getTags() 
{
   return tags;
}

int Node::getCount() 
{
   return count; 
}

Node* Node::getNext() 
{
   return next;
}

int Node::getSize() 
{
   return size;
}

/*********************************************************************
 *     LINKED LIST CLASS
 *********************************************************************/


/*********************************************************************
 * METHOD: Constructor
 * DESCRIPTION:
 * This constructs the list.
 * 
 * PARAMETER: 
 * The only parameter is the number of tags that need to be compared.
 * That is one current tag plus the nunber of previous tags to compare
 * to.
 *
 ********************************************************************/
LinkedList::LinkedList(int p_size)
   :head(NULL), nodeSize(p_size), totalOccur(0), types(0)
{
   // does nothing
}


void LinkedList::insert( string* tags )
{
   Node* temp = new Node(tags, nodeSize, 1 );
   totalOccur++;
   
   if ( head == NULL )
   {
      head = temp;
   }
   else
   {
      insert( head, temp );
   }
}


void LinkedList::insert( Node* curr, Node* node )
{
   if ( nodeCompare( curr, node) == true)
   {
      curr->countpp();
      delete node;
   }
   else
   {
      if ( curr->getNext() == NULL )
      {
	 curr->setNext(node);
	 types++;
      }
      else
      {
	 insert( curr->getNext(), node);
      }
   }   
}


bool LinkedList::write()
{
   Node* temp = head;
   string* tag = NULL;
   int i = 0;

   
   
   if ( temp == NULL ) {return false;};

   std::string s;
   std::stringstream out;
   out << nodeSize;
   s = out.str();

   string name = "OrderedPair";
   name += s;
   name += ".txt";
   
	char filename[25];
	strcpy( filename, name.c_str() );
   
   cout << " Filename: " << filename << endl;
   
   ofstream file_out(filename);
   if (file_out.fail())
   {
		cout << "Output file opening fialed." << endl;
		return false;
   }
   
   while ( temp != NULL )
   {
      tag = temp->getTags();
      
      for ( i = nodeSize - 1; i >= 0;  i-- )
      {
	 file_out << tag[i] << "\t";
      }
      file_out << temp->getCount() << "\n";
      
      temp = temp->getNext();
   }
   
   file_out.close();
   
   return true;
}



bool LinkedList::nodeCompare( Node* node1, Node* node2)
{
   if ( node1->getSize() != node2->getSize()  ) { return false;}
   
   string* temp1 = node1->getTags();
   string* temp2 = node2->getTags();

   for ( int i = 0; i < nodeSize; i++ )
   {
      if ( temp1[i] != temp2[i] ) {return false;}
   }

   return true;
}

unsigned int LinkedList::getTotalOccur()
{
	return totalOccur;
}

unsigned int LinkedList::getTypes()
{
	return types;
}

