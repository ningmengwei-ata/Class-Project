/* The MINIX model of memory allocation reserves a fixed amount of memory for
 * the combined text, data, and stack segments.  The amount used for a child
 * process created by FORK is the same as the parent had.  If the child does
 * an EXEC later, the new size is taken from the header of the file EXEC'ed.
 *
 * The layout in memory consists of the text segment, followed by the data
 * segment, followed by a gap (unused memory), followed by the stack segment.
 * The data segment grows upward and the stack grows downward, so each can
 * take memory from the gap.  If they meet, the process must be killed.  The
 * procedures in this file deal with the growth of the data and stack segments.
 *
 * The entry points into this file are:
 *   do_brk:	  BRK/SBRK system calls to grow or shrink the data segment
 *   adjust:	  see if a proposed segment adjustment is allowed
 *   size_ok:	  see if the segment sizes are feasible (i86 only)
 */

#include "pm.h"
#include <signal.h>
#include "mproc.h"
#include "param.h"
#include <lib.h>
#define DATA_CHANGED 1  /* flag value when data segment size changed */
#define STACK_CHANGED 2 /* flag value when stack size changed */

/*===========================================================================*
 *				do_brk  				     *
 *===========================================================================*/
PUBLIC int do_brk()
{
    /* Perform the brk(addr) system call.
 *
 * The call is complicated by the fact that on some machines (e.g., 8088),
 * the stack pointer can grow beyond the base of the stack segment without
 * anybody noticing it.
 * The parameter, 'addr' is the new virtual address in D space.
 */

    register struct mproc *rmp;
    int r;
    vir_bytes v, new_sp;
    vir_clicks new_clicks;

    rmp = mp;
    v = (vir_bytes)m_in.addr;
    new_clicks = (vir_clicks)(((long)v + CLICK_SIZE - 1) >> CLICK_SHIFT);
    if (new_clicks < rmp->mp_seg[D].mem_vir)
    {
        rmp->mp_reply.reply_ptr = (char *)-1;
        return (ENOMEM);
    }
    new_clicks -= rmp->mp_seg[D].mem_vir;
    if ((r = get_stack_ptr(who_e, &new_sp)) != OK) /* ask kernel for sp value */
        panic(__FILE__, "couldn't get stack pointer", r);
    r = adjust(rmp, new_clicks, new_sp);
    rmp->mp_reply.reply_ptr = (r == OK ? m_in.addr : (char *)-1);

    return (r); /* return new address or -1 */
}

/*===========================================================================*
 *			allocate_new_mem 		             *
 *===========================================================================*/
PUBLIC int allocate_new_mem(rmp, d_clicks, delta, clicks) register struct mproc *rmp;
phys_clicks d_clicks;
long delta;
phys_clicks clicks;
{
    register struct mem_map *mem_sp, *mem_dp;

    phys_bytes old_bytes, data_bytes;
    phys_bytes stak_bytes;
    phys_bytes old_d_tran, new_d_tran;
    phys_bytes old_s_tran, new_s_tran;
    phys_clicks old_clicks, old_base;
    phys_clicks new_clicks, new_base;
    phys_clicks new_s_base;
    phys_clicks old_s_base, stak_clicks;
    phys_clicks cur_s_base, cur_base;
    phys_clicks cur_clicks, cur_s_click;
    vir_clicks cur_v_base, len_old;
    int c, d, e, s;
    int changed, r, ft;
    changed = 0;
    mem_dp = &rmp->mp_seg[D];
    mem_sp = &rmp->mp_seg[S];
    len_old = mem_sp->mem_len + (mem_sp->mem_vir - mem_dp->mem_vir);

    /*alloc 2 times the size of the old space*/
    /* clicks=(phys_clicks)rmp->mp_seg[S].mem_len;
    clicks+=(rmp->mp_seg[S].mem_vir - rmp->mp_seg[D].mem_vir);*/
    old_clicks = clicks;
    new_clicks = clicks * 2;
    /*alloc new memory space*/
    /*judge whether the allocate success or not,if not success,don't free the old memory space*/
    if ((new_base = alloc_mem(new_clicks)) == NO_MEM)
    {
        printf("allocate ---error!");
        return (ENOMEM);
    }

    /*save the address and size of old memory space*/
    /*change clicks to bytes*/
    data_bytes = (phys_bytes)rmp->mp_seg[D].mem_len << CLICK_SHIFT;
    stak_bytes = (phys_bytes)rmp->mp_seg[S].mem_len << CLICK_SHIFT;

    old_base = rmp->mp_seg[D].mem_phys;
    old_s_base = rmp->mp_seg[S].mem_phys;
    /*the top of the stack*/
    new_s_base = new_base + new_clicks - mem_sp->mem_len;

    /*get the new stack and data address*/
    /*change address to phys_bytes*/
    new_d_tran = (phys_bytes)new_base << CLICK_SHIFT;
    old_d_tran = (phys_bytes)old_base << CLICK_SHIFT;
    new_s_tran = (phys_bytes)new_s_base << CLICK_SHIFT;
    old_s_tran = (phys_bytes)old_s_base << CLICK_SHIFT;

    /*copy the data and stack to the bottom and the top*/
    /*sys_abscopy copy from where ,copy to where,copy how much*/
    sys_memset(0, new_d_tran, (new_clicks << CLICK_SHIFT));

    d = sys_abscopy(old_d_tran, new_d_tran, data_bytes);
    if (d < 0)
        panic(__FILE__, " can't copy data segment in alloc_new_mem", d);
    s = sys_abscopy(old_s_tran, new_s_tran, stak_bytes);
    if (s < 0)
        panic(__FILE__, " can't copy stack segment in alloc_new_mem", s);
    /*save current address of data and stack*/
    cur_base = rmp->mp_seg[D].mem_phys;
    cur_s_base = rmp->mp_seg[S].mem_phys;
    cur_v_base = rmp->mp_seg[S].mem_vir;
    cur_s_click = mem_sp->mem_len;
    /*update the data stack of the process*/
    rmp->mp_seg[D].mem_phys = new_base;
    rmp->mp_seg[S].mem_phys = new_s_base;
    rmp->mp_seg[S].mem_vir = rmp->mp_seg[D].mem_vir + new_clicks - mem_sp->mem_len;
    /*rmp->mp_seg[S].mem_vir =rmp->mp_seg[D].mem_vir+ new_s_base-new_base;*/
    /* rmp->mp_seg[S].mem_vir +=len_old;*/
    /* Update data length (but not data orgin) on behalf of brk() system call. */
    cur_clicks = mem_dp->mem_len;

    if (d_clicks != mem_dp->mem_len)
    {
        mem_dp->mem_len = d_clicks;
        changed |= DATA_CHANGED;
    }
    /*printf("change data segment");*/
    /* Update stack length and origin due to change in stack pointer. */
    if (delta > 0)
    {
        printf("change stack segment\n");
        mem_sp->mem_vir -= delta;
        mem_sp->mem_phys -= delta;
        mem_sp->mem_len += delta;
        changed |= STACK_CHANGED;
    }

    /* Do the new data and stack segment sizes fit in the address space? */
    ft = (rmp->mp_flags & SEPARATE);
#if (CHIP == INTEL && _WORD_SIZE == 2)
    r = size_ok(ft, rmp->mp_seg[T].mem_len, rmp->mp_seg[D].mem_len,
                rmp->mp_seg[S].mem_len, rmp->mp_seg[D].mem_vir, rmp->mp_seg[S].mem_vir);
#else
    r = (rmp->mp_seg[D].mem_vir + rmp->mp_seg[D].mem_len >
         rmp->mp_seg[S].mem_vir)
            ? ENOMEM
            : OK;
#endif
    if (r == OK)
    {
        int r2;
        if (changed && (r2 = sys_newmap(rmp->mp_endpoint, rmp->mp_seg)) != OK)
            panic(__FILE__, "couldn't sys_newmap in adjust", r2);
        free_mem(old_base, old_clicks);
        printf("free the old memory space\n");
        return (OK);
    }
    printf("size not ok!\n");
    /* New sizes don't fit or require too many page/segment registers. Restore.*/
    if (changed & DATA_CHANGED)
    {

        mem_dp->mem_len = cur_clicks;
        mem_dp->mem_phys = cur_base;
    }

    if (changed & STACK_CHANGED)
    {
        printf("restore stack segment\n");
        mem_sp->mem_vir = cur_v_base;
        mem_sp->mem_phys = cur_s_base;
        mem_sp->mem_len = cur_s_click;
    }
    return (ENOMEM);

    /* if ((e = sys_newmap(rmp->mp_endpoint, rmp->mp_seg)) != OK)
        panic(__FILE__, "couldn't sys_newmap in alloc_new_mem", e);*/
}
/*===========================================================================*
 *				adjust  				     *
 *===========================================================================*/
PUBLIC int adjust(rmp, data_clicks, sp) register struct mproc *rmp; /* whose memory is being adjusted? */
vir_clicks data_clicks;                                             /* how big is data segment to become? */
vir_bytes sp;                                                       /* new value of sp */
{
    /* See if data and stack segments can coexist, adjusting them if need be.
 * Memory is never allocated or freed.  Instead it is added or removed from the
 * gap between data segment and stack segment.  If the gap size becomes
 * negative, the adjustment of data or stack fails and ENOMEM is returned.
 */

    register struct mem_map *mem_sp, *mem_dp;
    vir_clicks sp_click, gap_base, lower, old_clicks;
    int changed, r, ft;
    long base_of_stack, delta; /* longs avoid certain problems */
    int results;

    mem_dp = &rmp->mp_seg[D]; /* pointer to data segment map */
    mem_sp = &rmp->mp_seg[S]; /* pointer to stack segment map */
    changed = 0;              /* set when either segment changed */

    if (mem_sp->mem_len == 0)
        return (OK); /* don't bother init */

    /* See if stack size has gone negative (i.e., sp too close to 0xFFFF...) */
    base_of_stack = (long)mem_sp->mem_vir + (long)mem_sp->mem_len;
    sp_click = sp >> CLICK_SHIFT; /* click containing sp */
    if (sp_click >= base_of_stack)
        return (ENOMEM); /* sp too high */

    /* 新旧栈指针的差 栈向下生长的距离 */
    delta = (long)mem_sp->mem_vir - (long)sp_click;
    lower = (delta > 0 ? sp_click : mem_sp->mem_vir);

    /* Add a safety margin for future stack growth. Impossible to do right. */
#define SAFETY_BYTES (384 * sizeof(char *))
#define SAFETY_CLICKS ((SAFETY_BYTES + CLICK_SIZE - 1) / CLICK_SIZE)
    gap_base = mem_dp->mem_vir + data_clicks + SAFETY_CLICKS;

    /* data and stack collided realloc a bigger memory space*/
    if (lower < gap_base)
    {
        /*add the length of stack to the virtual address of stack
        to get the length of the memory space*/
        results = allocate_new_mem(rmp, data_clicks, delta, (phys_clicks)(rmp->mp_seg[S].mem_vir - rmp->mp_seg[D].mem_vir + rmp->mp_seg[S].mem_len));
        if (results == ENOMEM)
            return (ENOMEM);
        else if (results == OK)
            return (OK);
    }

    /* Update data length (but not data orgin) on behalf of brk() system call. */
    old_clicks = mem_dp->mem_len;
    if (data_clicks != mem_dp->mem_len)
    {
        mem_dp->mem_len = data_clicks;
        changed |= DATA_CHANGED;
    }

    /* Update stack length and origin due to change in stack pointer. */
    if (delta > 0)
    {

        mem_sp->mem_vir -= delta;
        mem_sp->mem_phys -= delta;
        mem_sp->mem_len += delta;
        changed |= STACK_CHANGED;
    }

    /* Do the new data and stack segment sizes fit in the address space? */
    ft = (rmp->mp_flags & SEPARATE);
#if (CHIP == INTEL && _WORD_SIZE == 2)
    r = size_ok(ft, rmp->mp_seg[T].mem_len, rmp->mp_seg[D].mem_len,
                rmp->mp_seg[S].mem_len, rmp->mp_seg[D].mem_vir, rmp->mp_seg[S].mem_vir);
#else
    r = (rmp->mp_seg[D].mem_vir + rmp->mp_seg[D].mem_len >
         rmp->mp_seg[S].mem_vir)
            ? ENOMEM
            : OK;
#endif
    if (r == OK)
    {
        int r2;
        if (changed && (r2 = sys_newmap(rmp->mp_endpoint, rmp->mp_seg)) != OK)

            panic(__FILE__, "couldn't sys_newmap in adjust", r2);

        return (OK);
    }

    /* New sizes don't fit or require too many page/segment registers. Restore.*/
    if (changed & DATA_CHANGED)
        mem_dp->mem_len = old_clicks;
    if (changed & STACK_CHANGED)
    {

        mem_sp->mem_vir += delta;
        mem_sp->mem_phys += delta;
        mem_sp->mem_len -= delta;
    }
    return (ENOMEM);
}

#if (CHIP == INTEL && _WORD_SIZE == 2)
/*===========================================================================*
 *				size_ok  				     *
 *===========================================================================*/
PUBLIC int size_ok(file_type, tc, dc, sc, dvir, s_vir) int file_type; /* SEPARATE or 0 */
vir_clicks tc;                                                        /* text size in clicks */
vir_clicks dc;                                                        /* data size in clicks */
vir_clicks sc;                                                        /* stack size in clicks */
vir_clicks dvir;                                                      /* virtual address for start of data seg */
vir_clicks s_vir;                                                     /* virtual address for start of stack seg */
{
    /* Check to see if the sizes are feasible and enough segmentation registers
 * exist.  On a machine with eight 8K pages, text, data, stack sizes of
 * (32K, 16K, 16K) will fit, but (33K, 17K, 13K) will not, even though the
 * former is bigger (64K) than the latter (63K).  Even on the 8088 this test
 * is needed, since the data and stack may not exceed 4096 clicks.
 * Note this is not used for 32-bit Intel Minix, the test is done in-line.
 */

    int pt, pd, ps; /* segment sizes in pages */

    pt = ((tc << CLICK_SHIFT) + PAGE_SIZE - 1) / PAGE_SIZE;
    pd = ((dc << CLICK_SHIFT) + PAGE_SIZE - 1) / PAGE_SIZE;
    ps = ((sc << CLICK_SHIFT) + PAGE_SIZE - 1) / PAGE_SIZE;

    if (file_type == SEPARATE)
    {
        if (pt > MAX_PAGES || pd + ps > MAX_PAGES)
            return (ENOMEM);
    }
    else
    {
        if (pt + pd + ps > MAX_PAGES)
            return (ENOMEM);
    }

    if (dvir + dc > s_vir)
        return (ENOMEM);

    return (OK);
}
#endif
