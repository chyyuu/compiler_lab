extern crate typed_arena;

use std::cell::{RefCell,Cell};
use std::fmt;
use typed_arena::Arena;

//simple example
struct NodeData<'a> {
    references: RefCell<Vec<TNode<'a>>>
}
type TNode<'a> = &'a NodeData<'a>;

//如添加Drop实现，它还是一个编译错误。
//编译此示例时怎么了？这与以下事实有关：首先调用节点0或节点1的析构函数。
//假设在node0之前销毁了node1。在这种情况下，当调用节点0的析构函数时，节点1的析构过程完成。但是，node0有一个指向node1的指针，因此它可以在node0的析构函数中搜索node1的废墟。当然，其中许多都是无效数据，当触摸它们时可能会爆炸。Typed Arena是drop check目的的一个很好的例子。
/*
impl<'a> Drop<'a> for NodeData<'a> {
    fn drop(&mut self) {}
}
*/


//在某些情况下，您也可以使用arena。arena保证存储在其中的值与arena本身具有相同的生存期。这意味着添加更多的值不会使任何现有生命周期无效，但会移动arena。因此，如果您需要返回树，则这种解决方案是不可行的。通过从节点本身删除所有权来解决此问题。这是一个示例，该示例还使用内部可变性来允许节点在创建后进行改变。在其他情况下，如果仅构造树然后简单地对其进行导航，则可以删除此可变性。
//tree
struct Tree<'a, T: 'a> {
    nodes: Arena<Node<'a, T>>,
}

impl<'a, T> Tree<'a, T> {
    fn new() -> Tree<'a, T> {
        Self {
            nodes: Arena::new(),
        }
    }
    fn new_node(&'a self, data: T) -> &'a mut Node<'a, T> {
        self.nodes.alloc(Node {
            data,
            tree: self,
            parent: Cell::new(None),
            children: RefCell::new(Vec::new()),
        })
    }
}

struct Node<'a, T: 'a> {
    data: T,
    tree: &'a Tree<'a, T>,
    parent: Cell<Option<&'a Node<'a, T>>>,
    children: RefCell<Vec<&'a Node<'a, T>>>,
}
impl<'a, T> Node<'a, T> {
    fn add_node(&'a self, data: T) -> &'a Node<'a, T> {
        let child = self.tree.new_node(data);
        child.parent.set(Some(self));
        self.children.borrow_mut().push(child);
        child
    }
}
impl<'a, T> fmt::Debug for Node<'a, T>
where    T: fmt::Debug
{
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{:?}", self.data)?;
        write!(f, " (")?;
        for c in self.children.borrow().iter() {
            write!(f, "{:?}, ", c)?;
        }
        write!(f, ")")
    }
}
    
//safe cycle
struct CycleParticipant<'a> {
    other: Cell<Option<&'a CycleParticipant<'a>>>,
}

fn main() {
    //simple example
    let nodes = Arena::new(); 
    let node0 = nodes.alloc(NodeData { references: RefCell::new(vec![]) });
    node0.references.borrow_mut().push(node0);
    let node1 = nodes.alloc(NodeData { references: RefCell::new(vec![]) });
    node0.references.borrow_mut().push(node1);



    //safe cycle
    let arena = Arena::new();

    let a = arena.alloc(CycleParticipant { other: Cell::new(None) });
    let b = arena.alloc(CycleParticipant { other: Cell::new(None) });
    a.other.set(Some(b));
    b.other.set(Some(a));
    
    //tree    
    let tree = Tree::new();
    let head = tree.new_node(1);
    let _left = head.add_node(2);
    let _right = head.add_node(3);
    println!("{:?}", head); // 1 (2 (), 3 (), )
}
