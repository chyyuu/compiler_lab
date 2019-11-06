extern crate typed_arena;

use std::cell::{RefCell,Cell};
use typed_arena::Arena;

struct NodeData<'a> {
    references: RefCell<Vec<Node<'a>>>
}
type Node<'a> = &'a NodeData<'a>;

//如添加Drop实现，它还是一个编译错误。
//编译此示例时怎么了？这与以下事实有关：首先调用节点0或节点1的析构函数。
//假设在node0之前销毁了node1。在这种情况下，当调用节点0的析构函数时，节点1的析构过程完成。但是，node0有一个指向node1的指针，因此它可以在node0的析构函数中搜索node1的废墟。当然，其中许多都是无效数据，当触摸它们时可能会爆炸。Typed Arena是drop check目的的一个很好的例子。
/*
impl<'a> Drop<'a> for NodeData<'a> {
    fn drop(&mut self) {}
}
*/
//safe cycle
struct CycleParticipant<'a> {
    other: Cell<Option<&'a CycleParticipant<'a>>>,
}

fn main() {
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
}
