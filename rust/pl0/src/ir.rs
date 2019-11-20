pub type Label = String;

#[derive(Clone, Debug, PartialEq)]
pub enum IR {
    JMP(Label),
    JMZ(Label),
    LOAD(Label),
    LOADC(i32),
    STORE(Label),
    CALL(Label),
    WRITE,
    ADD,
    SUB,
    DIV,
    MUL,
    ODD,
    LT,
    LTE,
    GT,
    GTE,
    EQ,
    NEQ,
    NOOP,
    StartFunc,
    RET,
    HALT,
    DEC(i32),
}

#[derive(Clone, Debug)]
pub struct Line {
    pub label: Option<Label>,
    pub inst: IR
}

impl Line {
    pub fn new(label: Option<Label>, inst: IR) -> Line {
        Line { label, inst }
    }
}
