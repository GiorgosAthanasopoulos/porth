	.text
	.globl main
main:
	sub $sp,$sp,4
	li $t0,100
	sw $t0,($sp)
	sub $sp,$sp,4
	li $t0,50
	sw $t0,($sp)
	lw $t0,($sp)
	add $sp,$sp,4
	lw $t1,($sp)
	add $sp,$sp,4
	mul $t2,$t0,$t1
	sub $sp,$sp,4
	sw $t2,($sp)
	sub $sp,$sp,4
	li $t0,26
	sw $t0,($sp)
	sub $sp,$sp,4
	li $t0,2
	sw $t0,($sp)
	lw $t0,($sp)
	add $sp,$sp,4
	lw $t1,($sp)
	add $sp,$sp,4
	div $t2,$t0,$t1
	sub $sp,$sp,4
	sw $t2,($sp)
	lw $t0,($sp)
	add $sp,$sp,4
	lw $t1,($sp)
	add $sp,$sp,4
	add $t2,$t0,$t1
	sub $sp,$sp,4
	sw $t2,($sp)
	lw $t0,($sp)
	add $sp,$sp,4
	move $a0,$t0
	li $v0,1
	syscall
exit:
	li $v0,10
	syscall
